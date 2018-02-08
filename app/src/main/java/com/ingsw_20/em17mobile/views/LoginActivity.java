package com.ingsw_20.em17mobile.views;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ingsw_20.em17mobile.R;
import com.ingsw_20.em17mobile.controller.AllViewController;
import com.ingsw_20.em17mobile.controller.LoginController;
import com.ingsw_20.em17mobile.model.LoginModel;

import java.util.Observable;
import java.util.Observer;

/**
 * Questa classe contiene la view responsabile per il login, necessario
 * per l'utilizzo dell'applicazione
 *
 * @author ingSW20
 * @see java.util.Observer
 */
public class LoginActivity extends AppCompatActivity implements Observer {
    /**
     * variabile per mostrare il caricamento durante il login
     */
    private ProgressDialog dialog;

    /**
     * Variabile utilizzata dal metodo onBackPressed() per evitare la chiusura
     * accidentale dell'applicazione
     */
    private long backPressedTime = 0;

    /**
     * campo di testo per l'inserimento dello username
     */
    private EditText usernameText;

    /**
     * campo di testo per l'inserimento della password
     */
    private EditText passwordText;

    /**
     * variabile per il controllore del login
     */
    private LoginController loginController;

    /**
     * Tag utile per il logger
     *
     * @see android.util.Log
     */
    private String TAG = "LoginActivityTAG";

    /**
     * metodo autogenerato per la creazione ed inizializzazione della view.
     * Contiene inoltre il listener del bottone che avvia la verifica delle credenziali tramite la classe LoginController
     *
     * @param savedInstanceState
     * @see LoginController
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        LoginModel.getInstance().addObserver(this);

        // memorizza le credenziali una volta eseguito l'accesso,
        // così le inserisce in automatico al prossimo avvio
        LoginModel.getInstance().setSharedPreferences(getApplicationContext());

        loginController = new LoginController();
        usernameText = findViewById(R.id.usernameText);
        passwordText = findViewById(R.id.passwordText);

        //imposta i campi user e password con le credenziali precedentemente utilizzate
        usernameText.setText(LoginModel.getInstance().getUser());
        passwordText.setText(LoginModel.getInstance().getPassword());

        Button loginButton = findViewById(R.id.loginButton);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(usernameText.getText()) || TextUtils.isEmpty(passwordText.getText())) {
                    printToastMessage("I campi Username e Password non possono essere vuoti");
                } else {
                    createDialog();
                    loginController.logIn(usernameText.getText().toString(), passwordText.getText().toString());
                }
            }
        });

        TextView passwordDimenticata = findViewById(R.id.passwordDimenticata);
        passwordDimenticata.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AllViewController.getInstance().toPasswordRecovery(LoginActivity.this);
            }
        });
    }

    /**
     * Questo metodo controlla la pressione del tasto indietro al fine di evitare
     * terminazioni accidentali dell'applicazione.
     * Nel caso la chiusura non è accidentale, chiama il metodo della superclasse
     *
     * @see AppCompatActivity#onBackPressed()
     */
    @Override
    public void onBackPressed() {        // to prevent irritating accidental logouts
        long t = System.currentTimeMillis();
        if (t - backPressedTime > 2000) {    // 2 secs
            backPressedTime = t;
            printToastMessage("Premere di nuovo per uscire dall'app");
        } else {
            super.onBackPressed();
        }
    }

    /**
     * wrongCredentials colora di rosso i campi username e password
     *
     * @see #usernameText
     * @see #passwordText
     */
    private void wrongCredentials() {
        usernameText.setBackgroundColor(Color.RED);
        passwordText.setBackgroundColor(Color.RED);
    }

    /**
     * il metodo update (interfaccia Observer) si occupa dell'aggiornamento della UI
     * nel caso di credenziali errate oppure comunica al controller che le credenziali sono corrette
     *
     * @param o   non utilizzato
     * @param arg non utilizzato
     * @see java.util.Observer
     */
    @Override
    public void update(Observable o, Object arg) {
        if (LoginModel.getInstance().isNetworkConnection()) {

            if (LoginModel.getInstance().getLogged()) {
                if (dialog != null) {
                    dialog.dismiss();
                }
                printToastMessage("LogIn eseguito con successo");
                AllViewController.getInstance().toMainActivity(this);
            } else {
                dialog.dismiss();
                wrongCredentials();
            }
        } else {
            dialog.dismiss();
            printToastMessage("Problemi con la connessione ad internet");
        }
    }

    /**
     * controlla se è stato fatto il logout nell'ultimo utilizzo dell'app,
     * dopodiché richiama il metodo della superclasse
     *
     * @see AppCompatActivity#onStart()
     */
    @Override
    protected void onStart() {
        loginController.checkLogin();
        super.onStart();
    }

    /**
     * questo metodo si occupa di stampare su schermo un messaggio di tipo Toast
     *
     * @param message messaggio da stampare
     */
    private void printToastMessage(String message) {
        Toast.makeText(LoginActivity.this, message, Toast.LENGTH_LONG).show();
    }

    private void createDialog() {
        dialog = ProgressDialog.show(LoginActivity.this, "Attendere per favore", "Login in corso...", true);
    }
}
