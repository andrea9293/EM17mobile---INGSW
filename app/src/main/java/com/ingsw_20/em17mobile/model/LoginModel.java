package com.ingsw_20.em17mobile.model;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Observable;
import java.util.Observer;

/**
 * La classe memorizza alcuni dati utili per il riconoscimento per i cambi di stato
 * dell'applicazione e per la memorizzazione delle credenziali d'accesso
 */

public class LoginModel extends Observable {
    /**
	 * Observer responsabile del ripristino password
	 */
	private Observer passRecovery;

	/**
     * flag utile per capire se l'emil utilizzata per il recupero password è giusta oppure no
     */
    private boolean rightEmail;

	/**
     * flag per il riconosccimento dello stato di connessione ad internet
     */
    private boolean networkConnection = true;

    /**
     * Variabile che memorizza l'istanza della classe corrente (Singleton)
     */
    private static final LoginModel ourInstance = new LoginModel();
    
	/**
     * Variabile per la gestione dei dati memorizzati dell'applicazione
     *
     * @see android.content.SharedPreferences
     */
    private SharedPreferences sharedPreferences;
    
	/**
     * identificativo per l'inizializzazione di sharedPreferences
     *
     * @see #sharedPreferences
     */
    private static final String PREFS_NAME = "ApplicationModelPreferences";

	/**
     * variabile che informa circa lo stato del login dell'utente
     */
    private Boolean isLogged;

    /**
     * Getter per l'istanza corrente della classe
     *
     * @return istanza corrente della classe
     */
    public static LoginModel getInstance() {
        return ourInstance;
    }

    /**
     * Costruttore vuoto
     */
    private LoginModel() {}

    /**
     * Metodo per l'inizializzazione di sharedPreferences
     *
     * @param context contesto dell'activity che necessita dei dati memorizzati
     * @see #sharedPreferences
     */
    public void setSharedPreferences(Context context) {
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, 0);
    }

    /**
     * Setter per l'user utilizzato per il login
     *
     * @param user username per il login
     */
    public void setUser(String user) {
        String userPref = "userPref";
        sharedPreferences.edit().putString(userPref, user).apply();
    }

    /**
     * getter per il dato memorizzato circa l'username per il login
     *
     * @return username per il login
     */
    public String getUser() {
        String userPref = "userPref";
        return sharedPreferences.getString(userPref, "");
    }

    /**
     * Setter per la password utilizzata per il login
     *
     * @param password password per il login
     */
    public void setPassword(String password) {
        String passwordPref = "passwordPref";
        sharedPreferences.edit().putString(passwordPref, password).apply();
    }

    /**
     * getter per il dato memorizzato circa la password per il login
     *
     * @return password per il login
     */
    public String getPassword() {
        String passwordPref = "passwordPref";
        return sharedPreferences.getString(passwordPref, "");
    }

    /**
     * getter per isLogged
     *
     * @return se "true" il login è stato effettuato, "false" altrimenti
     * @see #isLogged
     */
    public Boolean getLogged() {
        return isLogged;
    }

    /**
     * setter per isLogged che notifica anche gli Observers (LoginActivity)
     *
     * @param logged se "true" il login è stato effettuato, "false" altrimenti
     * @see com.ingsw_20.em17mobile.views.LoginActivity
     * @see #isLogged
     */
    public void setLogged(Boolean logged) {
        isLogged = logged;
        if (logged) {
            networkConnection = true;
        }
        setChanged();
        notifyObservers();
    }

    /**
     * Setter per il flag per il riconosccimento dello stato di connessione ad internet
     * @param networkConnection true se la connessione ad internet è presente, false altrimenti
     */
    public void setNetworkConnection(boolean networkConnection) {
        this.networkConnection = networkConnection;
        setChanged();
        notifyObservers();
    }

    /**
     * Getter per il flag per il riconosccimento dello stato di connessione ad internet
     * @return true se la connessione ad internet è presente, false altrimenti
     */
    public boolean isNetworkConnection() {
        return networkConnection;
    }

    /**
     * getter per il flag utile per capire se l'emil utilizzata per il recupero password è giusta oppure no
     * @return true se l'email è giusta, false altrimenti
     */
    public boolean isRightEmail() {
        return rightEmail;
    }

    /**
     * setter per il flag utile per capire se l'email utilizzata per il recupero password è giusta oppure no
     * @param rightEmail true se l'email è giusta, false altrimenti
     */
    public void setRightEmail(boolean rightEmail) {
        this.rightEmail = rightEmail;
        passRecovery.update(null,null); // notifico il cambiamento alla view
    }

	/**
	 * setter per l'observer responsabile del recupero password
	 */
    public void addPasswordRecoveryObserver(Observer observer) {
        this.passRecovery = observer;
    }
}
