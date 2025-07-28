package com.andrew.bean;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;

/**
 * SessionScoped bean for selecting X value
 */
@ManagedBean(name = "selectXBean", eager = true)
@SessionScoped
public class SelectXBean {
    private boolean selectedM3;
    private boolean selectedM2;
    private boolean selectedM1;
    private boolean selected0;
    private boolean selected1;
    private boolean selected2;
    private boolean selected3;
    
    @ManagedProperty(value="#{formBean}")
    FormBean formBean;

    /**
     * Default SelectXBean constructor
     */
    public SelectXBean() {
    }

    /**
     * Event when checkbox value changed
     * @param selected index of changed checkbox
     */
    public void checkboxValueChanged(int selected) {
        int sum = 0;
        boolean bools[] = {selectedM3, selectedM2, selectedM1, selected0, selected1, selected2, selected3};
        for (int i = -3; i <= 3; i++) {
            sum += bools[i+3] ? 1 : 0;
            if (i + 3 != selected) bools[i+3] = false;
        }

        if (sum == 0) {
            bools[selected+3] = false;

            formBean.setX(null);
        } else {
            if (selected == -3) {
                selectedM2 = selectedM1 = selected0 = selected1 = selected2 = selected3 = false;
                selectedM3 = true;
            } else if (selected == -2) {
                selectedM3 = selectedM1 = selected0 = selected1 = selected2 = selected3 = false;
                selectedM2 = true;
            } else if (selected == -1) {
                selectedM3 = selectedM2 = selected0 = selected1 = selected2 = selected3 = false;
                selectedM1 = true;
            } else if (selected == 0) {
                selectedM3 = selectedM2 = selectedM1 = selected1 = selected2 = selected3 = false;
                selected0 = true;
            } else if (selected == 1) {
                selectedM3 = selectedM2 = selectedM1 = selected0 = selected2 = selected3 = false;
                selected1 = true;
            } else if (selected == 2) {
                selectedM3 = selectedM2 = selectedM1 = selected0 = selected1 = selected3 = false;
                selected2 = true;
            } else if (selected == 3) {
                selectedM3 = selectedM2 = selectedM1 = selected0 = selected1 = selected2 = false;
                selected3 = true;
            }

            formBean.setX(Double.valueOf(selected));
        }

        formBean.renderPoint();
    }

    /**
     * Get FormBean
     * @return FormBean
     */
    public FormBean getFormBean() {
        return formBean;
    }

    /**
     * Set FormBean
     * @param formBean FormBean
     */
    public void setFormBean(FormBean formBean) {
        this.formBean = formBean;
    }

    /**
     * Is selected -3
     * @return bool
     */
    public boolean isSelectedM3() {
        return selectedM3;
    }

    /**
     * Set selected -3
     * @param selectedM3 bool
     */
    public void setSelectedM3(boolean selectedM3) {
        this.selectedM3 = selectedM3;
    }

    /**
     * Is selected -2
     * @return bool
     */
    public boolean isSelectedM2() {
        return selectedM2;
    }

    /**
     * Set selected -2
     * @param selectedM2 bool
     */
    public void setSelectedM2(boolean selectedM2) {
        this.selectedM2 = selectedM2;
    }

    /**
     * Is selected -1
     * @return bool
     */
    public boolean isSelectedM1() {
        return selectedM1;
    }

    /**
     * Set selected -1
     * @param selectedM1 bool
     */
    public void setSelectedM1(boolean selectedM1) {
        this.selectedM1 = selectedM1;
    }

    /**
     * Is selected 0
     * @return bool
     */
    public boolean isSelected0() {
        return selected0;
    }

    /**
     * Set selected 0
     * @param selected0 bool
     */
    public void setSelected0(boolean selected0) {
        this.selected0 = selected0;
    }

    /**
     * Is selected 1
     * @return bool
     */
    public boolean isSelected1() {
        return selected1;
    }

    /**
     * Set selected 1
     * @param selected1 bool
     */
    public void setSelected1(boolean selected1) {
        this.selected1 = selected1;
    }

    /**
     * Is selected 2
     * @return bool
     */
    public boolean isSelected2() {
        return selected2;
    }

    /**
     * Set selected 2
     * @param selected2 bool
     */
    public void setSelected2(boolean selected2) {
        this.selected2 = selected2;
    }

    /**
     * Is selected 3
     * @return bool
     */
    public boolean isSelected3() {
        return selected3;
    }

    /**
     * Set selected 3
     * @param selected3 bool
     */
    public void setSelected3(boolean selected3) {
        this.selected3 = selected3;
    }
}
