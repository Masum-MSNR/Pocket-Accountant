package com.me.pa.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.me.pa.R;
import com.me.pa.databinding.DialogImageCropperBinding;

public class ImageCropperDialog extends AppCompatDialogFragment {

    DialogImageCropperBinding binding;
    Context context;
    BitmapListener listener;
    Uri imageUri;

    public ImageCropperDialog(Context context, BitmapListener listener, Uri imageUri) {
        this.context = context;
        this.listener = listener;
        this.imageUri = imageUri;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity(), R.style.Dialog);
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_image_cropper, null);

        binding = DialogImageCropperBinding.bind(view);

        binding.cropImageView.setImageUriAsync(imageUri);

        binding.closeIbt.setOnClickListener(v -> dismiss());
        binding.okIbt.setOnClickListener(v -> {
            listener.getBitmap(binding.cropImageView.getCroppedImage());
            dismiss();
        });


        builder.setView(view);
        return builder.create();

    }

    public interface BitmapListener {
        void getBitmap(Bitmap bitmap);
    }
}

