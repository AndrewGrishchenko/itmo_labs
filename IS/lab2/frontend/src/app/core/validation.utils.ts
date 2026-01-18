import { Validators, ValidatorFn, AbstractControl, ValidationErrors } from "@angular/forms";

export class AppValidators {
    static integer: ValidatorFn = (control: AbstractControl): ValidationErrors | null => {
        if (control.value === null || control.value === '') {
            return null;
        }
        const isValid = /^[-]?\d+$/.test(control.value);
        return isValid ? null : { 'isNotInteger': true };
    };

    static float: ValidatorFn = (control: AbstractControl): ValidationErrors | null => {
        if (control.value === null || control.value === '') {
            return null;
        }
        const isValid = /^[-]?\d*([.]\d+)?$/.test(control.value);
        return isValid ? null : { 'isNotFloat': true };
    };

    static positive: ValidatorFn = (control: AbstractControl): ValidationErrors | null => {
        if (control.value === null || control.value === '') {
        return null;
        }
        return control.value > 0 ? null : { 'isNotPositive': true };
    };
}