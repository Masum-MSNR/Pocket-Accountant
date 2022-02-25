package com.me.pa.viewModels;

import androidx.lifecycle.ViewModel;

public class DialogAddAccountVM extends ViewModel {

    public boolean isAccountTitleValid(String accountTitle) {
        return accountTitle.length() > 5;
    }

    public boolean isNoOfPeopleValid(int noOfPeople) {
        return noOfPeople <= 5 && noOfPeople > 1;
    }

    public boolean isFormValid(boolean isAccountTitleValid, String accountType, String[] types) {
        return isAccountTitleValid && !accountType.equals("") && !accountType.equals(types[1]);
    }

    public boolean isFormValid(boolean isAccountTitleValid, boolean isNoOfPeopleValid) {
        return isAccountTitleValid && isNoOfPeopleValid;
    }


}
