package fr.leroymerlin.bylink.qualitycheck.basa;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BasaMNEntry {

    public String businessunitidentifier;
    public String departmentidentifier;
    public String departmentdescription;
    public String subdepartmentidentifier;
    public String subdepartmentdescription;
    public String typeidentifier;
    public String typedescription;
    public String subtypeidentifier;
    public String subtypedescription;
    public String status;

    @Override
    public String toString() {
        return "BasaMNEntry{" +
                "businessunitidentifier='" + businessunitidentifier + '\'' +
                ", departmentidentifier='" + departmentidentifier + '\'' +
                ", departmentdescription='" + departmentdescription + '\'' +
                ", subdepartmentidentifier='" + subdepartmentidentifier + '\'' +
                ", subdepartmentdescription='" + subdepartmentdescription + '\'' +
                ", typeidentifier='" + typeidentifier + '\'' +
                ", typedescription='" + typedescription + '\'' +
                ", subtypeidentifier='" + subtypeidentifier + '\'' +
                ", subtypedescription='" + subtypedescription + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
