package com.ingsw_20.em17mobile.controller;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.ingsw_20.em17mobile.views.PassRecoveryActivity;
import com.ingsw_20.em17mobile.views.LoginActivity;
import com.ingsw_20.em17mobile.views.MainActivity;

/**
 * AllViewController è una classe singleton che si occupa del passaggio da una view ad un'altra
 *
 * @author ingSW20
 */

public class AllViewController {
    /**
     * Variabile che memorizza l'istanza della classe corrente (Singleton)
     */
    private static final AllViewController ourInstance = new AllViewController();
    private String TAG = "AllViewControllerLOG";

    /**
     * Getter per l'istanza corrente della classe
     *
     * @return istanza corrente della classe
     */
    public static AllViewController getInstance() {
        return ourInstance;
    }

    /**
     * Costruttore vuoto
     */
    private AllViewController() {}

    /**
     * Passaggio da una view alla schermata principale (MainActivity)
     *
     * @param activity activity dalla quale si vuole uscire
     * @see com.ingsw_20.em17mobile.views.MainActivity
     */
    public void toMainActivity(AppCompatActivity activity) {
        Intent myIntent = new Intent(activity, MainActivity.class);

        // imposto i flag per l'eliminazione definitiva dell'activity precedente
        myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        // avvio la nuova activity
        activity.startActivity(myIntent);

        // elimino l'activity precedente
        activity.finishAndRemoveTask();
    }

    /**
     * Passaggio da una view alla schermata di login (LoginActivity)
     *
     * @param activity activity dalla quale si vuole uscire
     * @see com.ingsw_20.em17mobile.views.LoginActivity
     */
    public void toLoginActivity(AppCompatActivity activity) {
        Intent intent = new Intent(activity, LoginActivity.class);

        // imposto i flag per l'eliminazione definitiva dell'activity precedente
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        // avvio la nuova activity
        activity.startActivity(intent);

        // elimino l'activity precedente
        activity.finishAndRemoveTask();
    }

    /**
     * Passaggio da una view alla schermata per il recupero password (PassRecoveryActivity)
     *
     * @param activity activity dalla quale si vuole uscire
     * @see com.ingsw_20.em17mobile.views.PassRecoveryActivity
     */
    public void toPasswordRecovery(AppCompatActivity activity) {
        Intent intent = new Intent(activity, PassRecoveryActivity.class);

        // avvio la nuova activity
        activity.startActivity(intent);
    }
}
