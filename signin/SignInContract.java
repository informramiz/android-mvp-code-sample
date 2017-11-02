package com.hypercubesoft.boatdisplay.signin;

import com.hypercubesoft.boatdisplay.dependencyinjection.components.AppComponent;
import com.hypercubesoft.boatdisplay.entity.Squad;

import java.util.ArrayList;

/**
 * Created by ramiz on 9/24/16.
 * This class defines the contract between SignInView (Activity) and SignInPresenter
 */

public interface SignInContract {
    /**
     * following are actions that view needing this contract must implement
     */
    interface View {
        void gotoMainActivity(Squad selectedSquad);
        void gotoSignUpActivity();
        void setProgressIndicator(boolean state);
        void showSignInFailureMsg(String error);
        void showInvalidEmailMsg();
        void showInvalidPassword();
        void displaySquadsSelectionDialog(ArrayList<Squad> squads);
        void setUserActionListener(UserActionListener userActionListener);
        AppComponent provideAppComponent();
    }

    /**
     * following are actions that presenter needing this contract must implement
     */
    interface UserActionListener {
        void signIn(String username, String password);
        void squadSelected(Squad squad);
        void signUp();
        void forgotPassword();
    }
}
