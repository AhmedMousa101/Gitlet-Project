package gitlet;

import java.io.Serializable;

/**
* An object represents the branches
* @author Ahmed Mousa
*  */
public class Branch implements Serializable {
    /**
     * A branch should store:
     * - Name
     * - Sha of Head commit
     * */
    private String Name;
    private String Sha;
    public Branch(String Name, String Sha) {
        this.Name = Name;
        this.Sha = Sha;
    }

    /** A method to get the name of the branch */
    public String getName() {
        return Name;
    }

    /** A method to return the Sha of the head commit tracked by the branch */
    public String getSha() {
        return Sha;
    }

    /** A setter method to set the name */
    public void setName(String Name) {
        this.Name = Name;
    }

    /** A method to set the Sha */
    public void setSha(String Sha) {
        this.Sha = Sha;
    }
}
