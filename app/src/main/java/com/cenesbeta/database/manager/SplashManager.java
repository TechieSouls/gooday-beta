package com.cenesbeta.database.manager;


import com.cenesbeta.bo.Splash;

/**
 * Created by Neha on 29/04/2020.
 */

public interface SplashManager {

    public void addSplash(Splash splash);
    public Splash getSplash();
    public void deleteSplash();
}
