package com.ingsw_20.em17mobile.controller;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ingsw_20.em17mobile.model.LoginModel;
import com.ingsw_20.em17mobile.model.TicketModel;
import com.ingsw_20.em17mobile.views.LoginActivity;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Questa classe contiene tutti i metodi per l'interfacciamento
 * dell'applicazione al database
 *
 * @author ingSW20
 */

public class DBController {

    /**
     * referenza alla root del database
     */
    private DatabaseReference reference;

    /**
     * identificativo rilevato dallo scanner
     */
    private String id;

    /**
     * Variabile per la generazione delle referenze. Utilizzata in setReference() e
     * risultatoQuery(String)
     *
     * @see #risultatoQuery(String)
     */
    private FirebaseDatabase database;
    /**
     * Tag utile per il logger
     *
     * @see android.util.Log
     */
    private String TAG = "DBControllerTAG";

    /**
     * Variabile che memorizza l'istanza della classe corrente (Singleton)
     */
    @SuppressLint("StaticFieldLeak")
    private static final DBController ourInstance = new DBController();

    /**
     * Getter per l'istanza corrente della classe
     *
     * @return istanza corrente della classe
     */
    public static DBController getInstance() {
        return ourInstance;
    }

    private ValueEventListener valueEventListener = new ValueEventListener() {
        public void onDataChange(DataSnapshot snapshot) {
            if (!snapshot.exists()) {
                // il biglietto non è valido e lo comunico al model
                TicketModel.getInstance().setValidated(false);
                Log.e(TAG, "possibili errori di comunicazione con il database");
                disableListener();
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            Log.e(TAG, "errore database", databaseError.toException());
            disableListener();
        }
    };

    private ChildEventListener childEventListener = new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {
                try {
                    // creazione delle date per la verifica della validità del biglietto
                    Date dateNow = new Date();
                    DateFormat todayFormat = new SimpleDateFormat("dd/MM/yyyy");
                    Date today = new SimpleDateFormat("dd/MM/yyyy").parse(todayFormat.format(dateNow));

                    String eventEndDate = dataSnapshot.child("data").child("fine").getValue().toString() + " " + dataSnapshot.child("ora").child("fine").getValue().toString();
                    Date eventEndtTime = new SimpleDateFormat("dd/MM/yyyy HH:mm").parse(eventEndDate);

                    // controllo se la data è quella giusta
                    if (dateNow.after(eventEndtTime) || dateNow.before(today)) {
                        throw new NullPointerException();
                    }

                    //prelevo il nome dell'evento
                    String event = dataSnapshot.child("nome").getValue().toString();
                    TicketModel.getInstance().setEvent(event);

                    //prelevo il numero degli accessi inerenti al biglietto
                    Log.d(TAG, "numero biglietti: " + dataSnapshot.child("biglietti").child(id).child("accessi").getValue().toString());
                    String accessesNumber = dataSnapshot.child("biglietti").child(id).child("accessi").getValue().toString();
                    TicketModel.getInstance().setAccessesNumber(accessesNumber);

                    //prelevo il nome del titolare
                    Log.d(TAG, "nome: " + dataSnapshot.child("biglietti").child(id).child("nome").getValue().toString());
                    String name = dataSnapshot.child("biglietti").child(id).child("nome").getValue().toString();
                    TicketModel.getInstance().setName(name);

                    //prelevo il cognome del titolare
                    Log.d(TAG, "cognome: " + dataSnapshot.child("biglietti").child(id).child("cognome").getValue().toString());
                    String surname = dataSnapshot.child("biglietti").child(id).child("cognome").getValue().toString();
                    TicketModel.getInstance().setSurname(surname);

                    // comunico al model che il biglietto è valido
                    TicketModel.getInstance().setValidated(true);
                    disableListener();
                } catch (NullPointerException e) {
                    Log.e(TAG, "nullpointer", e);
                    // il biglietto non è valido e lo comunico al model
                    TicketModel.getInstance().setValidated(false);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String prevChildKey) {
            Log.d(TAG, "changed");
            // è stato modificato qualcosa nel path della referenza
        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {
            Log.d(TAG, "removed");
            // è stato eliminato qualcosa nel path della referenza
        }

        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String prevChildKey) {
            Log.d(TAG, "moved");
            // è stato spostao qualcosa nel path della referenza;
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            // è stato riscontrato qualche errore
            Log.d(TAG, databaseError.getMessage());
            disableListener();
        }
    };

    /**
     * In caso di prima istanza si crea una referenza al database e si imposta
     * in maniera tale da mantenere una copia locale
     */
    private DBController() {
        if (ourInstance == null) {
            database = FirebaseDatabase.getInstance();
            database.setPersistenceEnabled(true);
        }
    }

    /**
     * Il metodo registra i listener per intercettare modifiche al database e le sincronizza
     * con la copia locale del database
     */
    void risultatoQuery(final String id) {
        this.id = id;

        reference.addChildEventListener(childEventListener);
        reference.addValueEventListener(valueEventListener);
    }

    /**
     * metodo per spegnere i listeners dopo una query
     */
    private void disableListener(){
        reference.removeEventListener(childEventListener);
        reference.removeEventListener(valueEventListener);
    }

    /**
     * setter per la referenza al database e rende disponibile
     * una copia locale del database
     */
    void setReference(){
        // imposto la referenza ad uno specifico path del database
            reference = database.getReference("luogo")
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .child("Eventi");
            reference.keepSynced(true); // tengo aggiornata la copia locale del database
    }

    public void logOut() {
        new LoginController().logOut();
    }
}
