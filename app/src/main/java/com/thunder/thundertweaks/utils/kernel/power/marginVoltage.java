package com.thunder.thundertweaks.utils.kernel.power;

import android.content.Context;

import com.thunder.thundertweaks.fragments.ApplyOnBootFragment;
import com.thunder.thundertweaks.utils.Utils;
import com.thunder.thundertweaks.utils.root.Control;

public class marginVoltage {
    private static marginVoltage sIOInstance;

    public static marginVoltage getInstance() {
        if (sIOInstance == null) {
            sIOInstance = new marginVoltage();
        }
        return sIOInstance;
    }

    private static final String PERCENT_MARGIN = "/sys/power/percent_margin";
    private static final String BIG_PERCENT_MARGIN = PERCENT_MARGIN + "/big_margin_percent";
    private static final String MID_PERCENT_MARGIN = PERCENT_MARGIN + "/mid_margin_percent";
    private static final String LIT_PERCENT_MARGIN = PERCENT_MARGIN + "/lit_margin_percent";
    private static final String G3D_PERCENT_MARGIN = PERCENT_MARGIN + "/g3d_margin_percent";
    private static final String MIF_PERCENT_MARGIN = PERCENT_MARGIN + "/mif_margin_percent";
    private static final String MFC_PERCENT_MARGIN = PERCENT_MARGIN + "/mfc_margin_percent";
    private static final String NPU_PERCENT_MARGIN = PERCENT_MARGIN + "/npu_margin_percent";
    private static final String AUD_PERCENT_MARGIN = PERCENT_MARGIN + "/aud_margin_percent";
    private static final String CAM_PERCENT_MARGIN = PERCENT_MARGIN + "/cam_margin_percent";
    private static final String CP_PERCENT_MARGIN = PERCENT_MARGIN + "/cp_margin_percent";
    private static final String DISP_PERCENT_MARGIN = PERCENT_MARGIN + "/disp_margin_percent";
    private static final String FSYS0_PERCENT_MARGIN = PERCENT_MARGIN + "/fsys0_margin_percent";
    private static final String INT_PERCENT_MARGIN = PERCENT_MARGIN + "/int_margin_percent";
    private static final String INTCAM_PERCENT_MARGIN = PERCENT_MARGIN + "/intcam_margin_percent";
    private static final String IVA_PERCENT_MARGIN = PERCENT_MARGIN + "/iva_margin_percent";
    private static final String SCORE_PERCENT_MARGIN = PERCENT_MARGIN + "/score_margin_percent";

    public static boolean hasPercentMargin() {
        return Utils.existFile(PERCENT_MARGIN);
    }

    public void setBIGPercentMargin(int value, Context context) {
        run(Control.write(String.valueOf(value), BIG_PERCENT_MARGIN),
                BIG_PERCENT_MARGIN, context);
    }

    public static int getBIGPercentMargin() {
        return Utils.strToInt(Utils.readFile(BIG_PERCENT_MARGIN));
    }

    public static boolean hasBIGPercentMargin() {
        return Utils.existFile(BIG_PERCENT_MARGIN);
    }

    public void setMIDPercentMargin(int value, Context context) {
        run(Control.write(String.valueOf(value), MID_PERCENT_MARGIN),
                MID_PERCENT_MARGIN, context);
    }

    public static int getMIDPercentMargin() {
        return Utils.strToInt(Utils.readFile(MID_PERCENT_MARGIN));
    }

    public static boolean hasMIDPercentMargin() {
        return Utils.existFile(MID_PERCENT_MARGIN);
    }

    public void setLITPercentMargin(int value, Context context) {
        run(Control.write(String.valueOf(value), LIT_PERCENT_MARGIN),
                LIT_PERCENT_MARGIN, context);
    }

    public static int getLITPercentMargin() {
        return Utils.strToInt(Utils.readFile(LIT_PERCENT_MARGIN));
    }

    public static boolean hasLITPercentMargin() {
        return Utils.existFile(LIT_PERCENT_MARGIN);
    }

    public void setG3DPercentMargin(int value, Context context) {
        run(Control.write(String.valueOf(value), G3D_PERCENT_MARGIN),
                G3D_PERCENT_MARGIN, context);
    }

    public static int getG3DPercentMargin() {
        return Utils.strToInt(Utils.readFile(G3D_PERCENT_MARGIN));
    }

    public static boolean hasG3DPercentMargin() {
        return Utils.existFile(G3D_PERCENT_MARGIN);
    }

    public void setMIFPercentMargin(int value, Context context) {
        run(Control.write(String.valueOf(value), MIF_PERCENT_MARGIN),
                MIF_PERCENT_MARGIN, context);
    }

    public static int getMIFPercentMargin() {
        return Utils.strToInt(Utils.readFile(MIF_PERCENT_MARGIN));
    }

    public static boolean hasMIFPercentMargin() {
        return Utils.existFile(MIF_PERCENT_MARGIN);
    }

    public void setMFCPercentMargin(int value, Context context) {
        run(Control.write(String.valueOf(value), MFC_PERCENT_MARGIN),
                MFC_PERCENT_MARGIN, context);
    }

    public static int getMFCPercentMargin() {
        return Utils.strToInt(Utils.readFile(MFC_PERCENT_MARGIN));
    }

    public static boolean hasMFCPercentMargin() {
        return Utils.existFile(MFC_PERCENT_MARGIN);
    }

    public void setNPUPercentMargin(int value, Context context) {
        run(Control.write(String.valueOf(value), NPU_PERCENT_MARGIN),
                NPU_PERCENT_MARGIN, context);
    }

    public static int getNPUPercentMargin() {
        return Utils.strToInt(Utils.readFile(NPU_PERCENT_MARGIN));
    }

    public static boolean hasNPUPercentMargin() {
        return Utils.existFile(NPU_PERCENT_MARGIN);
    }

    public void setAUDPercentMargin(int value, Context context) {
        run(Control.write(String.valueOf(value), AUD_PERCENT_MARGIN),
                AUD_PERCENT_MARGIN, context);
    }

    public static int getAUDPercentMargin() {
        return Utils.strToInt(Utils.readFile(AUD_PERCENT_MARGIN));
    }

    public static boolean hasAUDPercentMargin() {
        return Utils.existFile(AUD_PERCENT_MARGIN);
    }

    public void setCAMPercentMargin(int value, Context context) {
        run(Control.write(String.valueOf(value), CAM_PERCENT_MARGIN),
                CAM_PERCENT_MARGIN, context);
    }

    public static int getCAMPercentMargin() {
        return Utils.strToInt(Utils.readFile(CAM_PERCENT_MARGIN));
    }

    public static boolean hasCAMPercentMargin() {
        return Utils.existFile(CAM_PERCENT_MARGIN);
    }

    public void setCPPercentMargin(int value, Context context) {
        run(Control.write(String.valueOf(value), CP_PERCENT_MARGIN),
                CP_PERCENT_MARGIN, context);
    }

    public static int getCPPercentMargin() {
        return Utils.strToInt(Utils.readFile(CP_PERCENT_MARGIN));
    }

    public static boolean hasCPPercentMargin() {
        return Utils.existFile(CP_PERCENT_MARGIN);
    }

    public void setDISPPercentMargin(int value, Context context) {
        run(Control.write(String.valueOf(value), DISP_PERCENT_MARGIN),
                DISP_PERCENT_MARGIN, context);
    }

    public static int getDISPPercentMargin() {
        return Utils.strToInt(Utils.readFile(DISP_PERCENT_MARGIN));
    }

    public static boolean hasDISPPercentMargin() {
        return Utils.existFile(DISP_PERCENT_MARGIN);
    }

    public void setFSYS0PercentMargin(int value, Context context) {
        run(Control.write(String.valueOf(value), FSYS0_PERCENT_MARGIN),
                FSYS0_PERCENT_MARGIN, context);
    }

    public static int getFSYS0PercentMargin() {
        return Utils.strToInt(Utils.readFile(FSYS0_PERCENT_MARGIN));
    }

    public static boolean hasFSYS0PercentMargin() {
        return Utils.existFile(FSYS0_PERCENT_MARGIN);
    }

    public void setINTPercentMargin(int value, Context context) {
        run(Control.write(String.valueOf(value), INT_PERCENT_MARGIN),
                INT_PERCENT_MARGIN, context);
    }

    public static int getINTPercentMargin() {
        return Utils.strToInt(Utils.readFile(INT_PERCENT_MARGIN));
    }

    public static boolean hasINTPercentMargin() {
        return Utils.existFile(INT_PERCENT_MARGIN);
    }

    public void setINTCAMPercentMargin(int value, Context context) {
        run(Control.write(String.valueOf(value), INTCAM_PERCENT_MARGIN),
                INTCAM_PERCENT_MARGIN, context);
    }

    public static int getINTCAMPercentMargin() {
        return Utils.strToInt(Utils.readFile(INTCAM_PERCENT_MARGIN));
    }

    public static boolean hasINTCAMPercentMargin() {
        return Utils.existFile(INTCAM_PERCENT_MARGIN);
    }

    public void setIVAPercentMargin(int value, Context context) {
        run(Control.write(String.valueOf(value), IVA_PERCENT_MARGIN),
                IVA_PERCENT_MARGIN, context);
    }

    public static int getIVAPercentMargin() {
        return Utils.strToInt(Utils.readFile(IVA_PERCENT_MARGIN));
    }

    public static boolean hasIVAPercentMargin() {
        return Utils.existFile(IVA_PERCENT_MARGIN);
    }

    public void setSCOREPercentMargin(int value, Context context) {
        run(Control.write(String.valueOf(value), SCORE_PERCENT_MARGIN),
                SCORE_PERCENT_MARGIN, context);
    }

    public static int getSCOREPercentMargin() {
        return Utils.strToInt(Utils.readFile(SCORE_PERCENT_MARGIN));
    }

    public static boolean hasSCOREPercentMargin() {
        return Utils.existFile(SCORE_PERCENT_MARGIN);
    }

    public static boolean supported() {
        return hasPercentMargin();
    }

    private static void run(String command, String id, Context context) {
        Control.runSetting(command, ApplyOnBootFragment.POWER, id, context);
    }
}
