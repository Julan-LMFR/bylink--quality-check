package fr.leroymerlin.bylink.qualitycheck;

import fr.leroymerlin.bylink.qualitycheck.basa.lifecycle.LifeCycleEnum;
import fr.leroymerlin.bylink.qualitycheck.basa.lifecycle.LifecycleChecker;
import fr.leroymerlin.bylink.qualitycheck.basa.nomenclature.NomenclatureChecker;
import fr.leroymerlin.bylink.qualitycheck.basa.offerconstruction.OfferConstructionChecker;
import fr.leroymerlin.bylink.qualitycheck.bylink.ByLinkChecker;
import fr.leroymerlin.bylink.qualitycheck.bylink.MongoChecker;
import fr.leroymerlin.bylink.qualitycheck.ckb.CKBEntry;
import fr.leroymerlin.bylink.qualitycheck.datasource.BasaMNExport;
import fr.leroymerlin.bylink.qualitycheck.datasource.BasaPCMDExport;
import fr.leroymerlin.bylink.qualitycheck.datasource.CKBExport;
import fr.leroymerlin.bylink.qualitycheck.datasource.DataSourceFactory;
import fr.leroymerlin.bylink.qualitycheck.step.product.ProductChecker;

import java.util.Set;
import java.util.SortedMap;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class Launcher {

    public static void main(String[] args) {

        /*************************
         *
         *  INPUT : Export files from LM repositories, ByLink product database and CKB
         *
         *************************/

        // CKB export file, generated with DataManager
        String ckbExportFile = "C:\\Users\\loic.philippe\\OneDrive - Lojelis\\Documents\\Leroy Merlin\\Produits\\Export Juillet 2024\\16\\CKB\\export-prod-16072024.xlsx";

        // Basa export files, retrieved in Google Drive : "BASA UPLOAD New"
        String basaPCMDExportFile = "C:\\Users\\loic.philippe\\OneDrive - Lojelis\\Documents\\Leroy Merlin\\Produits\\Export Juillet 2024\\15\\BASA\\basa_full_pcmd_frlm.csv";
        String basaMNExportFile = "C:\\Users\\loic.philippe\\OneDrive - Lojelis\\Documents\\Leroy Merlin\\Produits\\Export Juillet 2024\\15\\BASA\\basa_full_mn_frlm.csv";

        // Step export file, provided by Step team each Monday by email
        String stepExportFile = "C:\\Users\\loic.philippe\\OneDrive - Lojelis\\Documents\\Leroy Merlin\\Produits\\Export Juillet 2024\\15\\STEP\\csv.csv";

        // Important refs provided by Jordan Goettelmann
        String importantFile = "C:\\Users\\loic.philippe\\OneDrive - Lojelis\\Documents\\Leroy Merlin\\Produits\\Réintégration complète\\CKB\\produits-principaux.csv";

        // ByLink Mongo export file, that only contains *LastUpdatedDate to check emptyness, exported with Studio3T
        String mongoExportFile = "C:\\Users\\loic.philippe\\OneDrive - Lojelis\\Documents\\Leroy Merlin\\Produits\\Réintégration complète\\MONGO\\find_query.csv";
        String bylinkExportFile = "C:\\Users\\loic.philippe\\OneDrive - Lojelis\\Documents\\Leroy Merlin\\Produits\\Export ByLink\\bylink_export.csv";

        /*************************
         *
         *  REPOSITORY CHECKER : Used to detect gap on critical data between LM repositories and CKB
         *
         *************************/

        OfferConstructionChecker offerConstructionChecker = new OfferConstructionChecker(ckbExportFile, basaPCMDExportFile, true, 10000);
        NomenclatureChecker nomenclatureChecker = new NomenclatureChecker(ckbExportFile, basaPCMDExportFile, basaMNExportFile, true, 10000);
        ProductChecker productChecker = new ProductChecker(ckbExportFile, stepExportFile, true, 10000);
        LifecycleChecker lifecycleChecker = new LifecycleChecker(ckbExportFile, basaPCMDExportFile, true, 10000);

        /*************************
         *
         *  GENERAL CHECKER : Used to get product refs that exists in LM repositories
         *  It uses the CKB export, and removes every unknown refs in LM repositories
         *
         *************************/

         GeneralChecker generalChecker = new GeneralChecker(ckbExportFile, stepExportFile, basaPCMDExportFile, importantFile,true, 100000);

        /*************************
         *
         *  MONGO CHECKER : Used to check is product in ByLink database contains all the data
         *
         *************************/

         MongoChecker mongoChecker = new MongoChecker(ckbExportFile, stepExportFile, basaPCMDExportFile, importantFile, mongoExportFile, 1000);

        /*************************
         *
         *  BYLINK CHECKER : Used to detect gap on all data between ByLink product database and CKB
         *
         *************************/

        // ByLinkChecker byLinkChecker = new ByLinkChecker(ckbExportFile, mongoExportFile);
    }
}
