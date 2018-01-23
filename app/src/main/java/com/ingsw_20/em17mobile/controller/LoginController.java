package com.ingsw_20.em17mobile.controller;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.ingsw_20.em17mobile.model.LoginModel;

/**
 * Questa classe si occupa della verifica delle credenziali d'accesso
 * all'app interfacciandosi con il database
 *
 * @author ingSW20
 */

public class LoginController {
    /**
     * Tag utile per il logger
     *
     * @see android.util.Log
     */
    private static String TAG = "LoginControllerTAG";

    /**
     * Variabile per la gestione dell'autenticazione utilizzata in logIn e in logOut()
     *
     * @see #logIn(String, String)
     * @see #logOut()
     */
    private FirebaseAuth mAuth;

    /**
     * Costruttore vuoto
     */
    public LoginController() {}

	/**
	 * Il  metodo si occupa di inviare al server una richiesta per il reset della password 
	 * dell'account legato alla mail utilizzata
	 */

    public static void passwordRecovery(String email){
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
							// se il task è andato a buon fine, la maiil è stata inviata e lo comunico al model
                            LoginModel.getInstance().setRightEmail(true);
                        } else {
							// se qulcosa è andato storto (email errata principalmente), comunico 
							// al model che c'è stato un errore
                            LoginModel.getInstance().setRightEmail(false);
                        }
                    }
                });
    }

    /**
     * metodo per la verifica del login all'apertura dell'applicazione.
     * In caso positivo mostra direttamente la MainActivity (schermata con lo scanner)
     */
    public void checkLogin() {
        try {
            // tento di stampare il nome dell'utente.
            // Nel caso in cui l'utente sia connesso, l'applicazione reindirizzerà l'utnte nella schermata principale con lo scanner.
            Log.d(TAG, FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
            mAuth = FirebaseAuth.getInstance();
            // utile per aggiornare la copia locale del DB in caso ci si
            // trovi improvvisamente offline
            DBController.getInstance().setReference();

            // il biglietto è valido e lo comunico al model
            LoginModel.getInstance().setLogged(true);
        } catch (NullPointerException e) {
            // Nel caso in cui l'utente non sia connesso, l'utente vedrà semplicemente la schermata di login
            // in quanto la funzione cattura semplicemente l'errore
            Log.d(TAG, "non è ancora loggato");
        }
    }

    /**
     * Il metodo si occupa del controllo delle credenziali interfacciandosi con il database
     * notificandone il risultato nel model LoginModel, inoltre si occupa di memorizzare in
     * locale le credenziali corrette
     *
     * @param email    username utilizzata per il login
     * @param password password utilizzata per il login
     * @see LoginModel#setLogged(Boolean)
     * @see LoginModel#setUser(String)
     * @see LoginModel#setPassword(String)
     */
    public void logIn(final String email, final String password) {
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        try {
            currentUser.getUid();
            Log.d(TAG, "login diretto");
            LoginModel.getInstance().setLogged(true);

            // imposto la referenza utile per aggiornare la copia locale del DB in caso ci si
            // trovi improvvisamente offline
            DBController.getInstance().setReference();
        } catch (NullPointerException e) {
            Log.d(TAG, "eseguo il login");
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            // verifico se il login ha avuto successo
                            if (task.isSuccessful()) {
                                // salvo le credenziali per il login
                                LoginModel.getInstance().setUser(email);
                                LoginModel.getInstance().setPassword(password);

                                // informo il model che lo stato dell'applicazione è "loggato"
                                // che lo notificherà alla view LoginActivity
                                LoginModel.getInstance().setLogged(true);

                                // imposto la referenza utile per aggiornare la copia locale del DB in caso ci si
                                // utile per aggiornare la copia locale del DB in caso ci si
                                // trovi improvvisamente offline
                                DBController.getInstance().setReference();
                            } else {
                                // In base all'eccezione riscontrata verifica se il problema riguarda le
                                // credenziali non corrette oppure un problema di connessione
                                Log.w(TAG, "signInWithEmail:failure", task.getException());
                                if (task.getException() instanceof FirebaseNetworkException) {
                                    Log.e(TAG, "problemi di connessione", task.getException());
                                    LoginModel.getInstance().setNetworkConnection(false);
                                } else {
                                    Log.e(TAG, "credenziali non corrette?", task.getException());
                                }

                                // annullo la memorizzazione delle precedenti credenziali
                                LoginModel.getInstance().setUser("");
                                LoginModel.getInstance().setPassword("");

                                // informo il model che lo stato dell'applicazione è "non loggato"
                                // quindi le credenziali inserite non sono valide.
                                // Il model lo notificherà alla view LoginActivity
                                LoginModel.getInstance().setLogged(false);
                            }
                        }
                    });
        }
    }


    /**
     * metodo utilizzato per il logout dal database
     */
    public void logOut() {
        mAuth = FirebaseAuth.getInstance();
        mAuth.signOut();
    }

}
