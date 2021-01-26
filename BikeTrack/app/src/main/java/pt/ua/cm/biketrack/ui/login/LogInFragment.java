package pt.ua.cm.biketrack.ui.login;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import pt.ua.cm.biketrack.R;
import pt.ua.cm.biketrack.ui.history.HistoryDetailFragmentDirections;
import pt.ua.cm.biketrack.ui.profile.ProfileFragment;

public class LogInFragment extends Fragment implements View.OnClickListener {
    private TextView register;
    private EditText editTextEmail, editTextPassword;
    private Button signIn;
    public View v;
    private FirebaseAuth mAuth;
    private ProgressBar progressBar;
    SharedPreferences sharedPreferences;
    private LogInViewModel logInViewModel;
    private Boolean isFragmentVisible;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        logInViewModel =
                new ViewModelProvider(this).get(LogInViewModel.class);
        v = inflater.inflate(R.layout.fragment_login, container, false);

        mAuth = FirebaseAuth.getInstance();

        register = (TextView) v.findViewById(R.id.register);
        register.setOnClickListener(this);

        signIn = (Button) v.findViewById(R.id.signIn);
        signIn.setOnClickListener((View.OnClickListener) this);

        editTextEmail = (EditText) v.findViewById(R.id.email);
        editTextPassword = (EditText)v.findViewById(R.id.password);

        progressBar = (ProgressBar) v.findViewById(R.id.progressBar);

        isFragmentVisible = true;


        sharedPreferences = getActivity().getSharedPreferences("MySharedPref", Context.MODE_PRIVATE);
        String s = sharedPreferences.getString("userID","");
        if(!s.equals("")){
/*            Fragment fragment = new ProfileFragment(s);
            FragmentManager fragmentManager =((AppCompatActivity)v.getContext()).getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.nav_host_fragment,fragment)
                    .commit();*/
            if(v != null){
                LogInFragmentDirections.ActionNavigationLoginToNavigationProfile action = LogInFragmentDirections.actionNavigationLoginToNavigationProfile(s);
                action.setUserID(s);
                Navigation.findNavController(requireActivity(), R.id.nav_host_fragment).navigate(action);
            }
        }
        return v;
    }
    @Override
    public void onClick(View v) {

        switch (v.getId()){
            //if the user click register
            case R.id.register:
                //startActivity(new Intent(this,RegisterUserFragment.class));
                /*Fragment fragment = new RegisterUserFragment();
                FragmentManager fragmentManager =((AppCompatActivity)v.getContext()).getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.nav_host_fragment,fragment)
                        .commit();*/
                Navigation.findNavController(v).navigate(R.id.navigation_register);

               // System.out.println("chegou aquiiiii");
                break;
            case R.id.signIn:
                userLogic();
                break;
        }

    }

    private void userLogic() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if(email.isEmpty()){
            editTextEmail.setError("Email is invalid!");
            editTextEmail.requestFocus();
            return;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            editTextEmail.setError("Please enter a valid email ");
            editTextEmail.requestFocus();
            return;
        }
        if(password.isEmpty()){
            editTextPassword.setError("Password is required! ");
            editTextEmail.requestFocus();
            return;
        }
        if(password.length()<6){
            editTextPassword.setError("Min password length is 6 characters! ");
            editTextEmail.requestFocus();
            return;
        }
        progressBar.setVisibility(View.VISIBLE);

        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    //redirect to user profile
                   // startActivity(new Intent(MainActivity.this,ProfileActivity.class));
                    FirebaseUser user = mAuth.getCurrentUser();
                    SharedPreferences.Editor myEdit = sharedPreferences.edit();
                    myEdit.putString("userID",""+user.getUid());
                    myEdit.commit();
                    Toast.makeText(getContext(),"Authentication working",Toast.LENGTH_SHORT).show();
/*
                    Fragment fragment = new ProfileFragment();
                    FragmentManager fragmentManager =((AppCompatActivity)v.getContext()).getSupportFragmentManager();
                    fragmentManager.beginTransaction()
                            .replace(R.id.nav_host_fragment,fragment)
                            .commit();
*/

                    LogInFragmentDirections.ActionNavigationLoginToNavigationProfile action = LogInFragmentDirections.actionNavigationLoginToNavigationProfile(user.getUid());
                    action.setUserID(user.getUid());
                    Navigation.findNavController(v).navigate(action);
                }else{
                    //Toast.makeText(MainActivity.this, "Failed to login!Please check your credentials ",Toast.LENGTH_LONG).show();

                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        isFragmentVisible=true;
    }

    @Override
    public void onPause() {
        super.onPause();
        isFragmentVisible=false;
    }


}
