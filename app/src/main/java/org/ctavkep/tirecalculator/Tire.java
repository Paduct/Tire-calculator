package org.ctavkep.tirecalculator;

class Tire {
    private int mTireWidth;
    private int mAspectRation;
    private int mRimDiameter;
    private int mLoadIndex;
    private String mSpeedIndex;
    private int mMaxLoad;
    private int mMaxSpeed;
    private double[] mParameters;

    private static final int[] LOAD_VALUE = {
            140, 145, 150, 155, 160, 165, 170, 175, 180, 185, 190, 195, 200, 206, 212, 218, 224,
            230, 236, 243, 250, 257, 265, 272, 280, 290, 300, 307, 315, 325, 335, 345, 355, 365,
            375, 387, 400, 412, 425, 437, 450, 462, 475, 487, 500, 515, 530, 545, 560, 580, 600,
            615, 630, 650, 670, 690, 710, 730, 750, 775, 800, 825, 850, 875, 900, 925, 950, 975,
            1000, 1030, 1060, 1090, 1120, 1150, 1180, 1215, 1250, 1285, 1320, 1360, 1400, 1450,
            1500, 1550, 1600, 1650, 1700, 1750, 1800, 1850, 1900
    };

    private static final int[] SPEED_VALUE = {
            50, 60, 65, 70, 80, 90, 100, 110, 120, 130, 140, 150,
            160, 170, 180, 190, 200, 210, 240, 270, 300, 240
    };

    static final String[] SPEED_INDEX = {
            "B", "C", "D", "E", "F", "G", "J", "K", "L", "M", "N",
            "P", "Q", "R", "S", "T", "U", "H", "V", "W", "Y", "ZR"
    };

    void setTireWidth(int tireWidth) {
        mTireWidth = tireWidth;
    }

    void setRimDiameter(int rimDiameter) {
        mRimDiameter = rimDiameter;
    }

    void setAspectRation(int aspectRation) {
        mAspectRation = aspectRation;
    }

    void setLoadIndex(int loadIndex) {
        mLoadIndex = loadIndex;
    }

    void setSpeedIndex(String speedIndex) {
        mSpeedIndex = speedIndex;
    }

    int getMaxLoad() {
        return mMaxLoad;
    }

    void setMaxLoad(int position) {
        mMaxLoad = LOAD_VALUE[position];
    }

    int getMaxSpeed() {
        return mMaxSpeed;
    }

    void setMaxSpeed(int position) {
        mMaxSpeed = SPEED_VALUE[position];
    }

    double[] getParameters() {
        double radius = mRimDiameter * 25.39954;
        double diameter = (mTireWidth * mAspectRation / 100.0) * 2 + radius;
        double circle = diameter * 3.14;
        double height = mTireWidth * mAspectRation / 100.0;
        double rim = mTireWidth / 25.39954 - 1;
        double rpkm = 1000.0 / (circle / 1000.0);
        double revs_pm = 1666666.6666667 / circle;
        double patch = circle * 0.03 * mTireWidth / 100.0;

        mParameters = new double[] {height, rim, diameter, radius, circle, rpkm, revs_pm, patch};
        return mParameters;
    }

    double[] compareTo(Tire tire) {
        double[] difference = new double[mParameters.length];
        for (int i = 0; i < mParameters.length; i++) {
            difference[i] = mParameters[i] - tire.mParameters[i];
        }
        return difference;
    }

    String getSidewallLabel() {
        StringBuilder label = new StringBuilder();
        label.append(mTireWidth)
                .append("/")
                .append( mAspectRation)
                .append("R")
                .append(mRimDiameter)
                .append(" ")
                .append(mLoadIndex)
                .append(mSpeedIndex);
        return new String(label);
    }
}
