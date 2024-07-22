package fr.leroymerlin.bylink.qualitycheck;

import fr.leroymerlin.bylink.qualitycheck.ckb.CKBEntry;

public abstract class Checker {

    final String PROMO_REF = "49";

    protected boolean isRefLmProcessable(CKBEntry ckbEntry) {
        return !ckbEntry.getRefLm().startsWith(PROMO_REF);
    }
}
