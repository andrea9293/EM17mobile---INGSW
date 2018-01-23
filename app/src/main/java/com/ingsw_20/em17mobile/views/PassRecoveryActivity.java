package com.ingsw_20.em17mobile.views;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.ingsw_20.em17mobile.R;
import com.ingsw_20.em17mobile.controller.LoginController;
import com.ingsw_20.em17mobile.model.LoginModel;

import java.util.Observable;
import java.util.Observer;

/**
 * Questa la view responsabile del recupero password.
 * PassRecoveryActivity implementa l'interfaccia Observer utile per il design pattern MVC
 *
 * @author ingSW20
 * @see java.util.Observer
 */
public class PassRecoveryActivity extends AppCompatActivity implements Observer{

	/**
     * metodo autogenerato per la creazione ed inizializzazione della view
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_pass_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        LoginModel.getInstance().addPasswordRecoveryObserver(this);

        final EditText usernameText = findViewById(R.id.usernameTextPasswordRecovery);

        Button loginButton = findViewById(R.id.loginButtonPasswordRecovery);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(usernameText.getText())) {
                    printToastMessage("I campi Username e Password non possono essere vuoti");
                } else {
                    LoginController.passwordRecovery(usernameText.getText().toString());
                }
            }
        });
    }

    /**
     * questo metodo si occupa di stampare su schermo un messaggio di tipo Toast
     *
     * @param message messaggio da stampare
     */
    private void printToastMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
	
	/**
     * Metodo richiesto dall'interfaccia Observer. Si occupa dell'aggiornamento dei componenti della view
     * prelevando i dati direttamente da LoginModel
     *
     * @param o
     * @param arg
     * @see java.util.Observer
     * @see LoginModel
     */
    @Override
    public void update(Observable o, Object arg) {
        if (LoginModel.getInstance().isRightEmail()) {
            printToastMessage("Email inviata");
        } else {
            printToastMessage("L'email inserita non è valida");
        }
    }

	/**
	 * questo metodo viene invocato alla distruzione dell'activity, inoltre si occupa di 
	 * cancellare l'iscrizione della view nell'Observable 
	 */
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
