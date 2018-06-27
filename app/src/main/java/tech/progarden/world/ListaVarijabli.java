package tech.progarden.world;



class ListaVarijabli {

    String ImeKulture,senzorTipIme,vremeSenzor,OpisNotifikacije;
    int IdListaSenzora,IdSenzorTip,OdPodaciIdeal,DoPodaciIdeal,OdZutoIdeal,DoZutoIdeal,idSenzorIncr;
    int IdSenNotNotifikacija;
    float vrednostSenzor;


    public String getVremeSenzor() {
        return vremeSenzor;
    }

    public String getOpisNotifikacije() {
        return OpisNotifikacije;
    }

    public void setOpisNotifikacije(String opisNotifikacije) {
        OpisNotifikacije = opisNotifikacije;
    }

    public int getIdSenzorIncr() {
        return idSenzorIncr;
    }

    public void setIdSenzorIncr(int idSenzorIncr) {
        this.idSenzorIncr = idSenzorIncr;
    }

    public int getIdSenNotNotifikacija() {
        return IdSenNotNotifikacija;
    }

    public void setIdSenNotNotifikacija(int idSenNotNotifikacija) {
        IdSenNotNotifikacija = idSenNotNotifikacija;
    }

    public float getVrednostSenzor() {
        return vrednostSenzor;
    }

    public void setVrednostSenzor(float vrednostSenzor) {
        this.vrednostSenzor = vrednostSenzor;
    }

    public void setVremeSenzor(String vremeSenzor) {
        this.vremeSenzor = vremeSenzor;
    }

    public ListaVarijabli() {}

    public String getImeKulture() {
        return ImeKulture;
    }

    public void setImeKulture(String imeKulture) {
        ImeKulture = imeKulture;
    }

    public String getSenzorTipIme() {
        return senzorTipIme;
    }

    public void setSenzorTipIme(String senzorTipIme) {
        this.senzorTipIme = senzorTipIme;
    }

    public int getIdListaSenzora() {
        return IdListaSenzora;
    }

    public void setIdListaSenzora(int idListaSenzora) {
        IdListaSenzora = idListaSenzora;
    }

    public int getIdSenzorTip() {
        return IdSenzorTip;
    }

    public void setIdSenzorTip(int idSenzorTip) {
        IdSenzorTip = idSenzorTip;
    }

    public int getOdPodaciIdeal() {
        return OdPodaciIdeal;
    }

    public void setOdPodaciIdeal(int odPodaciIdeal) {
        OdPodaciIdeal = odPodaciIdeal;
    }

    public int getDoPodaciIdeal() {
        return DoPodaciIdeal;
    }

    public void setDoPodaciIdeal(int doPodaciIdeal) {
        DoPodaciIdeal = doPodaciIdeal;
    }

    public int getOdZutoIdeal() {
        return OdZutoIdeal;
    }

    public void setOdZutoIdeal(int odZutoIdeal) {
        OdZutoIdeal = odZutoIdeal;
    }

    public int getDoZutoIdeal() {
        return DoZutoIdeal;
    }

    public void setDoZutoIdeal(int doZutoIdeal) {
        DoZutoIdeal = doZutoIdeal;
    }
}
