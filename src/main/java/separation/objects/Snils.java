package separation.objects;

/**
 * Created by VSKryukov on 15.01.2016.
 */
public class Snils {

    private String csn;                         //Certificate SerialNumber
    // private String ssn;                         //Subject SerialNumber
    private String city;                        //Subject L
    private String cn;                          //Subject CN
    private String notBefore;                   //CertificateNotBefore
    private String notAfter;                    //CertificateNotAfter
    //private String organization;                //Subject O
    private String region;                      //Subject S
    // private String inn;                         //Subject|SubjectAltName INN
    private String snilsNumber;                 //Subject|SubjectAltName SNILS

    public String getCsn() {
        return csn;
    }

    public String getCity() {
        return city;
    }

    public String getCn() {
        return cn;
    }

    public String getNotBefore() {
        return notBefore;
    }

    public String getNotAfter() {
        return notAfter;
    }

    public String getRegion() {
        return region;
    }

    public String getSnilsNumber() {
        return snilsNumber;
    }

    public void setCsn(String csn) {
        this.csn = csn;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setCn(String cn) {
        this.cn = cn;
    }

    public void setNotBefore(String notBefore) {
        this.notBefore = notBefore;
    }

    public void setNotAfter(String notAfter) {
        this.notAfter = notAfter;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public void setSnilsNumber(String snilsNumber) {
        this.snilsNumber = snilsNumber;
    }

    public Snils(String[] arr) {
        this.csn = arr[0];
        StringBuilder adderss = new StringBuilder();

        for (int i=1;i<arr.length - 5;i++ ){
            adderss.append(" ").append(arr[i].trim());
        }
        this.city = adderss.toString().trim();
        this.cn = arr[arr.length - 5];
        this.notBefore = arr[arr.length - 4];
        this.notAfter = arr[arr.length - 3];
        this.region = arr[arr.length - 2];
        this.snilsNumber = arr[arr.length - 1];
    }

    @Override
    public String toString(){
        return new StringBuilder()

                .append(csn).append(";")
                .append(city).append(";")
                .append(cn).append(";")
                .append(notBefore).append(";")
                .append(notAfter).append(";")
                .append(region).append(";")
                .append(snilsNumber)
                .toString();
    }

    @Override
    public int hashCode() {
        return snilsNumber.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Snils snils = (Snils) o;
        if (!snilsNumber.equals(snils.snilsNumber)) return false;
        return true;
    }
}
