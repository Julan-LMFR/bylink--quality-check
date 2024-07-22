package fr.leroymerlin.bylink.qualitycheck.datasource;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

@Getter
@Setter
public class DataSourceFactory {

    public static CKBExport CKB_EXPORT;
    public static BasaMNExport BASA_MN_EXPORT;
    public static BasaPCMDExport BASA_PCMD_EXPORT;
    public static StepExport STEP_EXPORT;
    public static ImportantExport IMPORTANT_EXPORT;
    public static MongoExport MONGO_EXPORT;

    public static CKBExport getCkbExport(String ckbExportFile) {
        if (CKB_EXPORT == null) {
            CKB_EXPORT = new CKBExport(ckbExportFile);
        }
        return CKB_EXPORT;
    }

    public static BasaMNExport getBasaMnExport(String basaMNExportFile) {
        if (BASA_MN_EXPORT == null) {
            BASA_MN_EXPORT = new BasaMNExport(basaMNExportFile);
        }
        return BASA_MN_EXPORT;
    }

    public static BasaPCMDExport getBasaPcmdExport(String basaPCMDExportFile) {
        if (BASA_PCMD_EXPORT == null) {
            BASA_PCMD_EXPORT = new BasaPCMDExport(basaPCMDExportFile);
        }
        return BASA_PCMD_EXPORT;
    }

    public static StepExport getStepExport(String stepExportFile) {
        if (STEP_EXPORT == null) {
            STEP_EXPORT = new StepExport(stepExportFile);
        }
        return STEP_EXPORT;
    }

    public static ImportantExport getImportantExport(String importantFile) {
        if (IMPORTANT_EXPORT == null) {
            IMPORTANT_EXPORT = new ImportantExport(importantFile);
        }
        return IMPORTANT_EXPORT;
    }

    public static MongoExport getMongoExport(String mongoExportFile) {
        if (MONGO_EXPORT == null) {
            MONGO_EXPORT = new MongoExport(mongoExportFile);
        }
        return MONGO_EXPORT;
    }
}
