import {Component, OnInit} from '@angular/core';
import {
  FormsModule,
  ReactiveFormsModule,
  UntypedFormBuilder,
  UntypedFormGroup,
  Validators
} from "@angular/forms";
import {NgIf} from "@angular/common";
import {AuthRequest} from "../../dtos/auth-request";
import {AuthService} from "../../services/auth.service";
import {Router, RouterLink} from "@angular/router";
import {PasswordModule} from "primeng/password";
import {ButtonModule} from "primeng/button";
import {ToastModule} from "primeng/toast";
import {DividerModule} from "primeng/divider";
import {InputTextModule} from "primeng/inputtext";
import {AutoFocusModule} from "primeng/autofocus";
import {MessageService} from "primeng/api";

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [
    FormsModule,
    NgIf,
    ReactiveFormsModule,
    PasswordModule,
    ButtonModule,
    RouterLink,
    ToastModule,
    DividerModule,
    InputTextModule,
    AutoFocusModule
  ],
  templateUrl: './register.component.html',
  styleUrl: './register.component.scss'
})
export class RegisterComponent implements OnInit {
  registerForm: UntypedFormGroup;
  // After first submission attempt, for validation will start
  submitted = false;

  constructor(private formBuilder: UntypedFormBuilder, private authService: AuthService, private router: Router,
              private messageService: MessageService) {
    this.registerForm = this.formBuilder.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required,
        Validators.minLength(8),
        Validators.pattern('^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])[a-zA-Z0-9]+$')]],
      confirmPassword: ['', [Validators.required]]
    });
  }


  confirmPasswordMatchesPassword(): boolean {
    if (this.registerForm.controls.password.value != this.registerForm.controls.confirmPassword.value) {
      this.registerForm.controls.confirmPassword.setErrors({notSame: true});
      return false;
    }
    return true;
  }


  submitRegisterForm() {
    this.submitted = true;

    if (this.registerForm.valid) {
      if (!this.confirmPasswordMatchesPassword()) {
        this.messageService.add({severity: 'error', summary: 'Error', detail: 'Passwords do not match'});
        console.warn("Passwords do not match");
        return;
      }
      const authRequest: AuthRequest = new AuthRequest(this.registerForm.controls.email.value, this.registerForm.controls.password.value);
      this.registerUser(authRequest);
    } else {
      this.messageService.add({severity: 'error', summary: 'Error', detail: 'Invalid input'});
      console.warn("Invalid input");
    }
  }


  registerUser(authRequest: AuthRequest) {
    this.authService.registerUser(authRequest).subscribe({
      next: () => {
        this.router.navigate([""]);
      },
      error: error => {
        console.warn(error);
        this.messageService.add({severity: 'error', summary: 'Registration failed', detail: error.error});
      }
    });
  }

  ngOnInit() {
  }

}
