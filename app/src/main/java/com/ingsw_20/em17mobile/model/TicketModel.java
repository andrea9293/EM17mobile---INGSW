package com.ingsw_20.em17mobile.model;

import android.util.Log;

import java.util.Observable;

/**
 * Questa classe (Singleton) è responsabile per la memorizzazione dei dati  e per
 * la registrazione delle view all'interno.
 * TicketModel rappresenta una delle classi del model nel design pattern MVC
 *
 * @see java.util.Observable
 * @author ingSW20
 */

public class TicketModel extends Observable {
    /**
     * Variabile che memorizza l'istanza della classe corrente (Singleton)
     */
    private static final TicketModel ourInstance = new TicketModel();

    /**
     * Variabile per la memorizzazione del nome del titolare del biglietto scansionato
     */
    private String name;

    /**
     * Variabile per la memorizzazione del cognome del titolare del biglietto scansionato
     */
    private String surname;

    /**
     * Variabile per la memorizzazione del numero di accessi legati al biglietto scansionato
     */
    private String accessesNumber;

    /**
     * Variabile per la memorizzazione del nome dell'evento legato al biglietto scansionato
     */
    private String event;

    /**
     * Flag per l'effettivo controllo del biglietto scansionato
     */
    private Boolean isValidated;

    /**
     * Getter per l'istanza corrente della classe
     * @return istanza corrente della classe
     */
    public static TicketModel getInstance() {
        return ourInstance;
    }

    /**
     * Costruttore vuoto
     */
    private TicketModel() {}

    /**
     * setter per la variabile name contenente
     * notifica l'avvenuta modifica agli Observer
     *
     * @see com.ingsw_20.em17mobile.views.MainActivity
     * @param name name del titolare del biglietto
     */
    public void setName(String name){
        this.name = name;
        setChanged();
    }

    /**
     * setter per la variabile result contenente le informazioni sul biglietto.
     *
     * @see java.util.Observer
     */
    public String getName() {
        return name;
    }

    /**
     * getter per la variabile surname
	 * 
	 * @see #surname
     */

    public String getSurname() {
        return surname;
    }

    /**
     * setter per la variabile surname
	 * 
	 * @see #surname
	 * @param surname cognome del titolare del biglietto
     */
    public void setSurname(String surname) {
        this.surname = surname;
        setChanged();
    }

	
    /**
     * getter per la variabile accessesNumber
	 * 
	 * @see #accessesNumber
     */
    public String getAccessesNumber() {
        return accessesNumber;
    }

    /**
     * setter per la variabile accessesNumber
	 * 
	 * @see #accessesNumber
	 * @param accessesNumber numero di accessi legati al biglietto
     */
    public void setAccessesNumber(String accessesNumber) {
        this.accessesNumber = accessesNumber;
        setChanged();
    }

    /**
     * getter per la variabile event
	 * 
	 * @see #event
     */
    public String getEvent() {
        return event;
    }

    /**
     * setter per la variabile event
	 * 
	 * @see #event
	 * @param event nome dell'evento legato al biglietto
     */
    public void setEvent(String event) {
        this.event = event;
        setChanged();
    }

	
    /**
     * getter per la variabile isValidated
	 * 
	 * @see #isValidated
     */
    public Boolean getValidated() {
        return isValidated;
    }

    /**
     * setter per la variabile isValidated
	 * 
	 * @see #isValidated
	 * @param validated flag per la verifica dell'usufuibilità del biglietto
     */
    public void setValidated(Boolean validated) {
        isValidated = validated;
        setChanged();
        notifyObservers();
        Log.d("BigliettoTAG", "notifico");
    }
}
