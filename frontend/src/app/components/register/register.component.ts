import {Component, OnInit} from '@angular/core';
import {FormsModule, ReactiveFormsModule, UntypedFormBuilder, UntypedFormGroup, Validators} from "@angular/forms";
import {NgIf} from "@angular/common";
import {AuthRequest} from "../../dtos/auth-request";
import {AuthService} from "../../services/auth.service";
import {Router} from "@angular/router";
import {ToastrService} from "ngx-toastr";

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [
    FormsModule,
    NgIf,
    ReactiveFormsModule
  ],
  templateUrl: './register.component.html',
  styleUrl: './register.component.scss'
})
export class RegisterComponent implements OnInit{
  registerForm: UntypedFormGroup;
  // After first submission attempt, for validation will start
  submitted = false;
  error = false;
  errorMessage = "";

  constructor(private formBuilder: UntypedFormBuilder, private authService: AuthService, private router: Router,
              private notification: ToastrService) {
    this.registerForm = this.formBuilder.group({
      username: ['', [Validators.required]],
      password: ['', [Validators.required, Validators.minLength(8)]]
    });
  }

  submitRegisterForm() {
    this.submitted = true;
    if (this.registerForm.valid) {
      const authRequest: AuthRequest = new AuthRequest(this.registerForm.controls.username.value, this.registerForm.controls.password.value);
      this.registerUser(authRequest);
    } else {
      console.log("Invalid input");
    }
  }


  registerUser(authRequest: AuthRequest) {
    console.log("Try to register user: " + authRequest.email);
    this.authService.registerUser(authRequest).subscribe({
      next: () => {
        console.log("Successfully registered user: " + authRequest.email);
        this.router.navigate([""]);
      },
      error: error => {
        console.log("Could not register due to:");
        console.log(error);
        this.error = true;
        if (typeof error.erro === "object") {
          this.errorMessage = error.error.error;
        } else {
          this.errorMessage = error.error;
        }
      }
    });
  }

  vanishError() {
    this.error = false;
  }

  ngOnInit() {
  }

}
