package com.dcac.go4lunch.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import com.dcac.go4lunch.R;
import com.dcac.go4lunch.databinding.ActivityWelcomeBinding;
import com.dcac.go4lunch.viewModels.UserManager;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;
import java.util.List;

public class WelcomeActivity extends BaseActivity<ActivityWelcomeBinding> {

    private ActivityResultLauncher<Intent> signInOrUpLauncher;
    private FirebaseAuth mAuth;
    private UserManager userManager = UserManager.getInstance();

    protected ActivityWelcomeBinding getViewBinding() {
        return ActivityWelcomeBinding.inflate(getLayoutInflater());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth=FirebaseAuth.getInstance();

        signInOrUpLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {

                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        if (user != null) {

                            userManager.createUser(user.getUid());
                            showSnackBar(getString(R.string.connection_succeed));
                            launchMainActivity();

                        }
                    } else {
                        IdpResponse response = IdpResponse.fromResultIntent(result.getData());
                        if (response == null) {
                            showSnackBar(getString(R.string.error_authentication_canceled));
                        } else if (response.getError() != null) {
                            if (response.getError().getErrorCode() == ErrorCodes.NO_NETWORK) {
                                showSnackBar(getString(R.string.error_no_internet));
                            } else if (response.getError().getErrorCode() == ErrorCodes.UNKNOWN_ERROR) {
                                showSnackBar(getString(R.string.error_unknown_error));
                            }
                        }
                    }
                }
        );

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            launchMainActivity();
            finish();
            return;
        }

        initializeViews();


    }

    private void initializeViews() {
        binding.buttonSignIn.setEnabled(false);

        binding.emailInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                enableSignInIfReady();
            }
        });

        binding.passwordInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                enableSignInIfReady();
            }
        });

        binding.buttonSignIn.setOnClickListener(v -> signInUser());

        binding.signUpLink.setOnClickListener(v -> startMailAndGoogleActivity());

        TypedValue value = new TypedValue();
        this.getTheme().resolveAttribute(R.attr.password_logo, value, true);
        int passwordEyeVisibleDrawableResId = value.resourceId;
        this.getTheme().resolveAttribute(R.attr.password_logo_close, value, true);
        int passwordEyeInvisibleDrawableResId = value.resourceId;

        binding.passwordInput.setOnTouchListener((v, event) -> {
            final int RIGHT = 2;
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (event.getRawX() >= (binding.passwordInput.getRight() - binding.passwordInput.getCompoundDrawables()[RIGHT].getBounds().width())) {
                    int selection = binding.passwordInput.getSelectionEnd();
                    if (binding.passwordInput.getInputType() == (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD)) {
                        binding.passwordInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                        binding.passwordInput.setCompoundDrawablesWithIntrinsicBounds(0, 0, passwordEyeVisibleDrawableResId, 0);
                    } else {
                        binding.passwordInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                        binding.passwordInput.setCompoundDrawablesWithIntrinsicBounds(0, 0, passwordEyeInvisibleDrawableResId, 0);
                    }
                    binding.passwordInput.setSelection(selection);
                    return true;
                }
            }
            return false;
        });
    }

    private void signInUser() {
        String email = binding.emailInput.getText().toString();
        String password = binding.passwordInput.getText().toString();

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("SignIn", "signInWithEmail:success");
                        launchMainActivity();
                    } else {
                        Log.w("SignIn", "signInWithEmail:failure", task.getException());
                        Toast.makeText(this, R.string.authentication_failed, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void startMailAndGoogleActivity() {

        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build());

        Intent MailAndGoogleIntent = AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setTheme(R.style.Base_Theme_Go4Lunch)
                .setAvailableProviders(providers)
                .setIsSmartLockEnabled(false,true)
                .setLogo(R.drawable.go4lunch_header)
                .build();

        signInOrUpLauncher.launch(MailAndGoogleIntent);
    }

    private void enableSignInIfReady() {
        String email = binding.emailInput.getText().toString();
        String password = binding.passwordInput.getText().toString();
        binding.buttonSignIn.setEnabled(!email.isEmpty() && !password.isEmpty());
    }

    private void launchMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void showSnackBar(String message) {
        Snackbar.make(binding.welcomeActivityLayout, message, Snackbar.LENGTH_SHORT).show();
    }
}
