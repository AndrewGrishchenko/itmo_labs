import { Validators } from "@angular/forms";

export const AppValidators = {
    integer: Validators.pattern(/^[-]?\d+$/),
    float: Validators.pattern(/^[-]?\d*([.]\d+)?$/),
    positive: Validators.min(0.0000000001),
    positiveFloat: Validators.pattern(/^\d*([.]\d+)?$/)
}