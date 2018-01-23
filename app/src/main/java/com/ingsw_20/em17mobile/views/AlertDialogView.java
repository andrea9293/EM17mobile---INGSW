package com.ingsw_20.em17mobile.views;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ingsw_20.em17mobile.R;
import com.ingsw_20.em17mobile.controller.ScanController;
import com.ingsw_20.em17mobile.model.TicketModel;

import java.util.Observable;
import java.util.Observer;

/**
 * Questa classe si occupa della gestione di un AlertDialog per la visualizzazione dei dettagli del biglietto scansionato.
 * Viene creato l'oggetto dalla classe MainActivity nel suo metodo onCreate().
 * AlertDialogView implementa l'interfaccia Observer utile per il design pattern MVC
 *
 * @author ingSW20
 * @see MainActivity#onCreate(Bundle)
 * @see java.util.Observer
 */

public class AlertDialogView implements Observer {
    /**
	 * FragmentManager utilizzato per l visulizzazione del popup
	 */
	static FragmentManager fragmentManager = null;
    
	/**
     * Tag utile per il logger
     *
     * @see android.util.Log
     */
    private static String TAG = "AlertDialogViewLOG";
    
	/**
     * Variabile per la gestione della scansione dei codici
     */
    private static ScanController scanController;
    
	/**
     * Sottoclasse per la creazione dell'AlertDialog
     *
     * @see Alert
     * @see AlertDialog
     */
    private Alert alert;

    /**
     * Costruttore per l'inizializzazione della classe. Si occupa dell'assegnazione delle variabili
     * mainActivity e scanController, inoltre registra la view nel model secondo i canoni del
     * design pattern proprio dell'MVC
     *
     * @param mainActivity
     * @param scanController
     */
    AlertDialogView(MainActivity mainActivity, ScanController scanController) {
        TicketModel.getInstance().addObserver(this);
        AlertDialogView.scanController = scanController;

        alert = new Alert();
    }

    /**
     * Metodo richiesto dall'interfaccia Observer. Si occupa dell'aggiornamento dei componenti della view
     * prelevando i dati direttamente da TicketModel
     *
     * @param o
     * @param arg
     * @see java.util.Observer
     * @see com.ingsw_20.em17mobile.model.TicketModel
     */
    @Override
    public void update(Observable o, Object arg) {
        if (alert.getFragmentManager() == null) {
            if (!alert.isAdded()) {
                alert.show(fragmentManager, TAG);
            }
        }
    }

	/**
	 * questo metodo viene invocato subito prima della distruzione della MainActivity e si occupa di 
	 * cancellare l'iscrizione della view nell'Observable 
	 */
    void onDestroy() {
        fragmentManager = null;
        TicketModel.getInstance().deleteObserver(this);
    }


    /**
     * Classe per la generazione dell'AlertDialog ed estende la classe DialogFragment
     *
     * @author Andrea Bravaccino
     * @see android.support.v4.app.DialogFragment
     */
    public static class Alert extends DialogFragment {
        /**
         * Questa TextView contiene le informazioni sul biglietto e viene
         * aggiornata tramite il metodo update
         *
         * @see #update(Observable, Object)
         */
        private TextView resulText;
        /**
         * ImageView che mostra se il biglietto scansioniato è valido o meno.
         * Viene aggiornata tramite il metodo update
         */
        private ImageView imageView;

        /**
         * Costruttore che va a chiamare il construttore della superclasse DialogFragment
         *
         * @see android.support.v4.app.DialogFragment
         */
        public Alert() {
            super();
        }

        /**
         * Metodo autogenerato per la creazione del dialog
         *
         * @param savedInstanceState
         * @return ritorna il Dialog che verrà successivamente mostrato grazie al metodo update
         * @see #update(Observable, Object)
         */
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            if (TicketModel.getInstance().getValidated()) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                        .setTitle("Biglietto valido!")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface arg0, int arg1) {
                            }
                        });

                LayoutInflater layoutInflater = getActivity().getLayoutInflater();
                LinearLayout view = (LinearLayout) layoutInflater.inflate(R.layout.ticket_result_dialog, null);
                imageView = new ImageView(getActivity().getApplicationContext());
                resulText = new TextView(getActivity().getApplicationContext());

                if (TicketModel.getInstance().getValidated()) {
                    String event = "Evento: " + TicketModel.getInstance().getEvent() + "\n";
                    String ticketHolder = "Biglietto intestao a " + TicketModel.getInstance().getName() + " " + TicketModel.getInstance().getSurname() + "\n";
                    String accessesNumber = "Numero di accessi: " + TicketModel.getInstance().getAccessesNumber();

                    String ticket = event + ticketHolder + accessesNumber;
                    resulText.setText(ticket);
                    imageView.setImageDrawable(this.getResources().getDrawable(R.drawable.ok_96px));

                }

                view.addView(imageView);
                view.addView(resulText);

                builder.setView(view);
                return builder.create();
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                        .setTitle("Biglietto NON valido!")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface arg0, int arg1) {
                            }
                        });

                LayoutInflater layoutInflater = getActivity().getLayoutInflater();
                LinearLayout view = (LinearLayout) layoutInflater.inflate(R.layout.ticket_result_dialog, null);
                imageView = new ImageView(getActivity().getApplicationContext());
                resulText = new TextView(getActivity().getApplicationContext());

                resulText.setText(R.string.biglietto_non_valido);
                imageView.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.cancel_96px));

                view.addView(imageView);
                view.addView(resulText);

                builder.setView(view);
                return builder.create();
            }
        }

        /**
         * Questo metodo autogenerato gestisce il dialog quando viene nascosto riprendendo la scansione
         * della fotocamera per il QR code
         *
         * @param dialog
         */
        @Override
        public void onDismiss(DialogInterface dialog) {
            scanController.resumeScan();
            super.onDismiss(dialog);
            super.onDestroy();
        }
    }
}


