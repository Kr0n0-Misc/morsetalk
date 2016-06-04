package com.blueta.morsetransmitter;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public class MorseCodeElement implements Parcelable {
    public static final Creator<MorseCodeElement> CREATOR;
    private static final long serialVersionUID = 1;
    public String Character;
    public String MorseCode;
    public int index;

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(this.index);
        out.writeString(this.Character);
        out.writeString(this.MorseCode);
    }

    static {
        CREATOR = new Creator<MorseCodeElement>() {
            public MorseCodeElement createFromParcel(Parcel in) {
                return new MorseCodeElement(in);
            }

            public MorseCodeElement[] newArray(int size) {
                return new MorseCodeElement[size];
            }
        };
    }

    private MorseCodeElement(Parcel in) {
        this.index = in.readInt();
        this.Character = in.readString();
        this.MorseCode = in.readString();
    }
}
