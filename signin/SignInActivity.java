package com.hypercubesoft.boatdisplay.signin;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.hypercubesoft.boatdisplay.BuildConfig;
import com.hypercubesoft.boatdisplay.activity.BaseActivity;
import com.hypercubesoft.boatdisplay.home.MainActivity;
import com.hypercubesoft.boatdisplay.home.OldMainActivity;
import com.hypercubesoft.boatdisplay.util.CommonUtils;
import com.hypercubesoft.boatdisplay.R;
import com.hypercubesoft.boatdisplay.dependencyinjection.components.AppComponent;
import com.hypercubesoft.boatdisplay.entity.Squad;

import java.util.ArrayList;


public class SignInActivity extends BaseActivity implements SignInContract.View {
    private final static String TAG = SignInActivity.class.getSimpleName();
    
    private Button mSignInButton;
    private Button mOfflineButton;
    private TextView mCreateNewAccTextView;
    private TextView mForgotPasswordTextView;
    private TextInputLayout mEmailTextInputLayout;
    private TextInputLayout mPasswordTextInputLayout;
    ProgressDialog mProgressDialog;
    
    @NonNull
    private SignInContract.UserActionListener mUserActionListener;
    
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        
        mEmailTextInputLayout = (TextInputLayout) findViewById(R.id.sign_in_edit_text_email);
        mPasswordTextInputLayout = (TextInputLayout) findViewById(R.id.sign_in_edit_text_password);
        mSignInButton = (Button) findViewById(R.id.sign_in_button_login);
        mCreateNewAccTextView = (TextView) findViewById(R.id.sign_in_text_view_create_new_account);
        mForgotPasswordTextView = (TextView) findViewById(R.id.sign_in_text_view_forgot_password);
        mOfflineButton = (Button) findViewById(R.id.signIn_button_offline);
        
        registerViewListeners();
        
        mUserActionListener = new SignInPresenter(this);
        setupProgressDialog();
        
        if (!CommonUtils.isConnectionAvailable(this)) {
            CommonUtils.showToastLong(this, getString(R.string.msg_no_internet));
        }
    }
    
    private void setupProgressDialog() {
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage(getString(R.string.msg_verifying));
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setCancelable(false);
    }
    
    private void registerViewListeners() {
        mSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!CommonUtils.isConnectionAvailable(SignInActivity.this)) {
                    CommonUtils.showSnackbarLong(mSignInButton, getString(R.string.msg_no_internet));
                    return;
                }
                String username = mEmailTextInputLayout.getEditText().getText().toString();
                String password = mPasswordTextInputLayout.getEditText().getText().toString();
                mUserActionListener.signIn(username, password);
            }
        });
        
        mOfflineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mUserActionListener.offlineModeButtonClicked();
            }
        });
        
        mCreateNewAccTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCreateAccountDialog();
            }
        });
        
        mForgotPasswordTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showForgotPasswordDialog();
            }
        });
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
    
    private void showCreateAccountDialog() {
        final EditText username = new EditText(this);
        username.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        final AlertDialog createNewAccDialog = new AlertDialog.Builder(SignInActivity.this)
        .setTitle(R.string.dialog_title_create_new_account)
        .setCancelable(false)
        .setView(username)
        //.setView(password)
        .setNegativeButton(R.string.dialog_create_account_negative_button_name, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                dialog.dismiss();
            }
        })
        .setPositiveButton(R.string.dialog_create_account_positive_button_name, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                //TODO: post to server
                dialog.dismiss();
            }
        }).create();
        createNewAccDialog.show();
    }
    
    private void showForgotPasswordDialog() {
        final EditText username = new EditText(this);
        username.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        final AlertDialog forgotPwDialog = new AlertDialog.Builder(SignInActivity.this)
        .setTitle(R.string.dialog_forgot_password_title)
        .setMessage(R.string.dialog_forgot_password_msg)
        .setView(username)
        .setCancelable(false)
        .setNegativeButton(R.string.dialog_forgot_password_negative_button_name, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                dialog.dismiss();
            }
        })
        .setPositiveButton(R.string.dialog_forgot_password_positive_button_name, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                Toast.makeText(getApplicationContext(), "TODO: Send email.",Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        }).create();
        forgotPwDialog.show();
    }
    
    /**
     * This method is called by presenter to notify the view to goto MainActivity
     * @param selectedSquad selected squad object
     * @param isOfflineMode tells whether app is in offline mode or online mode
     */
    @Override
    public void gotoMainActivity(@Nullable Squad selectedSquad, boolean isOfflineMode) {
        Intent intent = new Intent(SignInActivity.this, MainActivity.class);
        intent.putExtra(MainActivity.EXTRA_IS_OFFLINE_MODE , isOfflineMode);
        if (!isOfflineMode && selectedSquad != null) {
            intent.putExtra(MainActivity.EXTRA_SQUAD_ID, selectedSquad.id);
        }
        startActivity(intent);
        finish();
    }
    
    @Override
    public void gotoSignUpActivity() {
        
    }
    
    @Override
    public void setProgressIndicator(boolean isEnabled) {
        if (isEnabled) {
            mProgressDialog.show();
        } else {
            mProgressDialog.dismiss();
        }
    }
    
    /**
     * This method will be called by SignInPresenter when sign in api fails either due to server error or due to
     * invalid user credentials
     * @param errorMsg cause of failure
     */
    @Override
    public void showSignInFailureMsg(String errorMsg) {
        CommonUtils.showToastLong(this, getString(R.string.msg_sign_in_failed) + errorMsg);
    }
    
    /**
     * This method will be called by SignInPresenter when user has entered invalid email
     */
    @Override
    public void showInvalidEmailMsg() {
        CommonUtils.showSnackbarLong(mSignInButton, getString(R.string.msg_invalid_email));
    }
    
    /**
     * This method will be called by SignInPresenter when user has entered invalid password
     */
    @Override
    public void showInvalidPassword() {
        CommonUtils.showSnackbarLong(mSignInButton, getString(R.string.msg_invalid_password));
    }
    
    /**
     * This method will be called by SignInPresenter when signIn api call was successful
     * and we have received squads list. Presenter pass this squads list to this view
     * so that view can show a selection dialog to user
     * @param squads list of squads to diplay to user
     */
    @Override
    public void displaySquadsSelectionDialog(final ArrayList<Squad> squads) {
        SquadsListAdapter squadsListAdapter = new SquadsListAdapter(this,
                                                                    android.R.layout.simple_list_item_1, squads);
        
        AlertDialog.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                mUserActionListener.squadSelected(squads.get(which));
            }
        };
        
        final AlertDialog squadsAlertDialog = new AlertDialog.Builder(this)
        .setTitle(R.string.dialog_squads_title)
        .setCancelable(false)
        .setAdapter(squadsListAdapter, onClickListener)
        .create();
        
        squadsAlertDialog.show();
    }
    
    /**
     * This method will be called by SignInPresenter to register itself as a action listener on this
     * view
     * @param userActionListener reference of presenter
     */
    @Override
    public void setUserActionListener(SignInContract.UserActionListener userActionListener) {
        mUserActionListener = userActionListener;
    }
    
    /**
     * This method will be called by SignInPresenter to get AppComponent which in turn presenter
     * uses to notify dagger that it needs injection of components
     * @return
     */
    @Override
    public AppComponent provideAppComponent() {
        return getAppComponent();
    }
}
