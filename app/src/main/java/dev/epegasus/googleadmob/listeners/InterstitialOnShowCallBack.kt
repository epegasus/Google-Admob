package com.pdfapp.pdfreader.allpdf.pdfviewer.helper.interfaces.admob

interface InterstitialOnShowCallBack {
    fun onAdDismissedFullScreenContent()
    fun onAdFailedToShowFullScreenContent()
    fun onAdShowedFullScreenContent()
    fun onAdImpression()
}