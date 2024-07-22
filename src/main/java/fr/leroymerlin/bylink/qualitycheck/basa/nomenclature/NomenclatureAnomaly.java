package fr.leroymerlin.bylink.qualitycheck.basa.nomenclature;

public class NomenclatureAnomaly {

    public String refLm;
    public String ckbDepartmentValue;
    public String basaDepartmentValue;
    public String ckbSubDepartmentValue;
    public String basaSubDepartmentValue;
    public String ckbTypeValue;
    public String basaTypeValue;
    public String ckbSubTypeValue;
    public String basaSubTypeValue;

    public NomenclatureAnomaly(String refLm, String ckbDepartmentValue, String basaDepartmentValue, String ckbSubDepartmentValue, String basaSubDepartmentValue, String ckbTypeValue, String basaTypeValue, String ckbSubTypeValue, String basaSubTypeValue) {
        this.refLm = refLm;
        this.ckbDepartmentValue = ckbDepartmentValue;
        this.basaDepartmentValue = basaDepartmentValue;
        this.ckbSubDepartmentValue = ckbSubDepartmentValue;
        this.basaSubDepartmentValue = basaSubDepartmentValue;
        this.ckbTypeValue = ckbTypeValue;
        this.basaTypeValue = basaTypeValue;
        this.ckbSubTypeValue = ckbSubTypeValue;
        this.basaSubTypeValue = basaSubTypeValue;
    }

    @Override
    public String toString() {
        return refLm + ";" + ckbDepartmentValue + ";" + basaDepartmentValue + ";" + ckbSubDepartmentValue + ";" + basaSubDepartmentValue + ";" + ckbTypeValue + ";" + basaTypeValue + ";" + ckbSubTypeValue + ";" + basaSubTypeValue;
    }
}
