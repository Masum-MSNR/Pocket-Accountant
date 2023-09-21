package com.me.pa.dialogs;

import static com.me.pa.others.Functions.hideKeyBoard;
import static com.me.pa.others.Functions.isConnected;
import static com.me.pa.others.Functions.showKeyBoard;
import static com.me.pa.others.Constants.FDC_IS_COMPLETE_ACCOUNT;
import static com.me.pa.others.Constants.FDR_USER_ACCOUNTS;
import static com.me.pa.others.Constants.IS_DATA_LOADED;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.core.content.ContextCompat;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.annotations.NotNull;
import com.me.pa.R;
import com.me.pa.activities.AccountInfo;
import com.me.pa.activities.Home;
import com.me.pa.databinding.DialogOtpBinding;
import com.me.pa.models.UserAccount;
import com.me.pa.others.DismissListener;
import com.me.pa.others.Functions;
import com.me.pa.others.TriggerListener;
import com.me.pa.repos.OnBoardingRepo;
import com.me.pa.repos.UserRepo;

import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class OtpDialog extends AppCompatDialogFragment {

    Context context;
    String number;
    FirebaseAuth auth;
    DialogOtpBinding binding;
    OnBoardingRepo onBoardingRepo;
    UserRepo loggedInUserRepo;
    PhoneAuthProvider.ForceResendingToken token;
    ConnectionErrorDialog connectionErrorDialog;
    DismissListener listener;
    TriggerListener triggerListener;
    String otp;
    boolean isPinViewFilled = false;
    Locale language;


    public OtpDialog(Context context, String number, DismissListener listener, TriggerListener triggerListener) {
        this.context = context;
        this.number = number;
        this.listener = listener;
        this.triggerListener = triggerListener;
        language = Locale.forLanguageTag(UserRepo.getInstance().getLanguage());
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity(), R.style.Dialog);
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_otp, null);
        auth = FirebaseAuth.getInstance();
        onBoardingRepo = OnBoardingRepo.getInstance();
        loggedInUserRepo = UserRepo.getInstance();
        connectionErrorDialog = new ConnectionErrorDialog(context);

        binding = DialogOtpBinding.bind(view);
        if (onBoardingRepo.getLocal().equals("en")) {
            String ts = getString(R.string.otp_send_command) + "  <b>" + number + "</b>";
            binding.tv1.setText(Html.fromHtml(ts));
        } else {
            String ts = " <b>" + number + "</b>  " + getString(R.string.otp_send_command);
            binding.tv1.setText(Html.fromHtml(ts));
        }

        binding.pinView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                isPinViewFilled = charSequence.length() == 6;
                binding.verifyBt.setTextColor(isPinViewFilled ? getResources().getColor(android.R.color.white, null) : getResources().getColor(R.color.gray, null));
                binding.verifyBt.setBackgroundTintList(isPinViewFilled ? ContextCompat.getColorStateList(context, R.color.color_primary) : ContextCompat.getColorStateList(context, R.color.light_gray));
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        binding.verifyBt.setOnClickListener(v -> {
            if (!isPinViewFilled) {
                return;
            }
            hideKeyBoard(context, binding.verifyBt);
            completeVerification();
        });

        binding.closeBt.setOnClickListener(v -> dismiss());

        binding.resendBt.setOnClickListener(v -> {
            if (!isConnected(context)) {
                triggerListener.onTrigger();
                dismiss();
                return;
            }
            hideKeyBoard(context, binding.resendBt);
            binding.cl2.setVisibility(View.VISIBLE);
            binding.cl1.setVisibility(View.GONE);
            binding.timeCounterTv.setVisibility(View.VISIBLE);
            binding.resendBt.setVisibility(View.GONE);
            resendOtp(number, token);
        });

        sendOtp(number);


        builder.setView(view);
        AlertDialog dialog = builder.create();
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogPopAnimation;
        return dialog;
    }


    public void sendOtp(String number) {
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(auth)
                        .setPhoneNumber(number)
                        .setTimeout(60L, TimeUnit.SECONDS)
                        .setActivity((Activity) context)
                        .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                            @Override
                            public void onVerificationCompleted(@NonNull @NotNull PhoneAuthCredential phoneAuthCredential) {
                                String newOtp = phoneAuthCredential.getSmsCode();
                                binding.pinView.setText(newOtp);
                                hideKeyBoard(context, binding.pinView);
                                completeVerification();
                            }

                            @Override
                            public void onVerificationFailed(@NonNull @NotNull FirebaseException e) {
                                triggerListener.onTrigger();
                                dismiss();
                            }

                            @Override
                            public void onCodeSent(@NonNull @NotNull String s, @NonNull @NotNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                                super.onCodeSent(s, forceResendingToken);
                                onCodeSentF(s, forceResendingToken);
                            }
                        })
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    public void resendOtp(String number, PhoneAuthProvider.ForceResendingToken token) {
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(auth)
                        .setPhoneNumber(number)
                        .setTimeout(60L, TimeUnit.SECONDS)
                        .setActivity((Activity) context)
                        .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                            @Override
                            public void onVerificationCompleted(@NonNull @NotNull PhoneAuthCredential phoneAuthCredential) {
                                String newOtp = phoneAuthCredential.getSmsCode();
                                binding.pinView.setText(newOtp);
                                completeVerification();
                            }

                            @Override
                            public void onVerificationFailed(@NonNull @NotNull FirebaseException e) {
                                triggerListener.onTrigger();
                                dismiss();
                            }

                            @Override
                            public void onCodeSent(@NonNull @NotNull String s, @NonNull @NotNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                                super.onCodeSent(s, forceResendingToken);
                                onCodeSentF(s, forceResendingToken);
                            }
                        })
                        .setForceResendingToken(token)
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    public void completeVerification() {
        String iOtp = Objects.requireNonNull(binding.pinView.getText()).toString();
        if (otp != null) {
            binding.cl2.setVisibility(View.VISIBLE);
            binding.cl1.setVisibility(View.GONE);
            PhoneAuthCredential phoneAuthCredential = PhoneAuthProvider.getCredential(otp, iOtp);
            FirebaseAuth.getInstance().signInWithCredential(phoneAuthCredential)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            loggedInUserRepo.setNumber(number);
                            loggedInUserRepo.setLanguage(onBoardingRepo.getLocal());
                            loggedInUserRepo.setCompleteAccount(false);
                            FirebaseDatabase database = FirebaseDatabase.getInstance();
                            database.getReference(FDR_USER_ACCOUNTS)
                                    .child(number)
                                    .child(FDC_IS_COMPLETE_ACCOUNT).get().addOnCompleteListener(task1 -> {
                                if (task1.isSuccessful()) {
                                    if (Objects.requireNonNull(task1.getResult()).exists()) {
                                        Boolean isCompleteAccount = task1.getResult().getValue(Boolean.class);
                                        loggedInUserRepo.setCompleteAccount(Objects.requireNonNull(isCompleteAccount));
                                        if (isCompleteAccount) {
                                            database.getReference(FDR_USER_ACCOUNTS)
                                                    .child(number)
                                                    .get().addOnCompleteListener(task2 -> {
                                                if (task2.isSuccessful()) {
                                                    UserAccount account = Objects.requireNonNull(task2.getResult()).getValue(UserAccount.class);
                                                    loggedInUserRepo.setName(Objects.requireNonNull(account).getFullName());
                                                    loggedInUserRepo.setLanguage(account.getLanguage());
                                                    Functions.changeLanguage(context, account.getLanguage());
                                                    loggedInUserRepo.setCompleteAccount(account.isCompleteAccount());
                                                    loggedInUserRepo.initialCommit(context);
                                                    loggedInUserRepo.finalCommit(context);
                                                    Intent intent = new Intent(context, Home.class);
                                                    intent.putExtra(IS_DATA_LOADED, true);
                                                    startActivity(intent);
                                                    listener.onDismiss();
                                                } else {
                                                    //failed to retrieve data from server
                                                    triggerListener.onTrigger();
                                                }
                                                dismiss();
                                            });
                                        } else {
                                            loggedInUserRepo.initialCommit(context);
                                            startActivity(new Intent(context, AccountInfo.class));
                                            listener.onDismiss();
                                            dismiss();
                                        }
                                    } else {
                                        loggedInUserRepo.initialCommit(context);
                                        startActivity(new Intent(context, AccountInfo.class));
                                        listener.onDismiss();
                                        dismiss();
                                    }
                                } else {
                                    //failed to retrieve data from server
                                    triggerListener.onTrigger();
                                    dismiss();
                                }
                            });
                        } else {
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                binding.cl2.setVisibility(View.GONE);
                                binding.cl1.setVisibility(View.VISIBLE);
                                binding.pinView.requestFocus();
                                showKeyBoard(context, binding.pinView);
                                Toast.makeText(context, "Please enter correct OTP!", Toast.LENGTH_SHORT).show();
                            } else {
                                triggerListener.onTrigger();
                                dismiss();
                            }
                        }
                    });
        } else {
            triggerListener.onTrigger();
            dismiss();
        }
    }

    private void reverseTimer(final TextView tv) {

        new CountDownTimer(60000, 1000) {

            public void onTick(long l) {
                String ts;
                if (onBoardingRepo.getLocal().equals("en")) {
                    ts = context.getString(R.string.resend_otp_command) + " " + String.format(language, "%2d", TimeUnit.MILLISECONDS.toSeconds(l) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(l)) + 1) + "s";
                } else {
                    ts = String.format(language, "%2d", TimeUnit.MILLISECONDS.toSeconds(l) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(l)) + 1) + "s" + " " + context.getString(R.string.resend_otp_command);
                }
                tv.setText(ts);
            }

            public void onFinish() {
                binding.timeCounterTv.setVisibility(View.GONE);
                binding.resendBt.setVisibility(View.VISIBLE);
            }
        }.start();
    }

    private void onCodeSentF(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
        binding.pinView.requestFocus();
        showKeyBoard(context, binding.pinView);
        token = forceResendingToken;
        otp = s;
        binding.cl2.setVisibility(View.GONE);
        binding.cl1.setVisibility(View.VISIBLE);
        reverseTimer(binding.timeCounterTv);
        binding.timeCounterTv.setVisibility(View.VISIBLE);
        binding.resendBt.setVisibility(View.GONE);
    }
}
