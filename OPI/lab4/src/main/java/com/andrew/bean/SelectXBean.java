package com.andrew.bean;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;

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

    public SelectXBean() {
    }

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

    public FormBean getFormBean() {
        return formBean;
    }

    public void setFormBean(FormBean formBean) {
        this.formBean = formBean;
    }

    public boolean isSelectedM3() {
        return selectedM3;
    }

    public void setSelectedM3(boolean selectedM3) {
        this.selectedM3 = selectedM3;
    }

    public boolean isSelectedM2() {
        return selectedM2;
    }

    public void setSelectedM2(boolean selectedM2) {
        this.selectedM2 = selectedM2;
    }

    public boolean isSelectedM1() {
        return selectedM1;
    }

    public void setSelectedM1(boolean selectedM1) {
        this.selectedM1 = selectedM1;
    }

    public boolean isSelected0() {
        return selected0;
    }

    public void setSelected0(boolean selected0) {
        this.selected0 = selected0;
    }

    public boolean isSelected1() {
        return selected1;
    }

    public void setSelected1(boolean selected1) {
        this.selected1 = selected1;
    }

    public boolean isSelected2() {
        return selected2;
    }

    public void setSelected2(boolean selected2) {
        this.selected2 = selected2;
    }

    public boolean isSelected3() {
        return selected3;
    }

    public void setSelected3(boolean selected3) {
        this.selected3 = selected3;
    }
}
