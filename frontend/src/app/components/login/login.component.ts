import {Component, OnInit} from '@angular/core';
import {FormControl, FormGroup, UntypedFormBuilder, UntypedFormGroup, Validators} from '@angular/forms';
import {Router} from '@angular/router';
import {AuthService} from '../../services/auth.service';
import {AuthRequest} from '../../dtos/auth-request';
import {MessageService} from "primeng/api";


@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss'],
  providers: [MessageService]
})
export class LoginComponent implements OnInit {

  loginForm: FormGroup;

  submitted = false;
  // Error flag
  email: any;

  constructor(private formBuilder: UntypedFormBuilder, private authService: AuthService, private router: Router, private messageService: MessageService) {
  }

  ngOnInit() {
    this.loginForm = new FormGroup({
      email: new FormControl('', [Validators.required]),
      password: new FormControl('', [Validators.required])
    });
  }
  /**
   * Form validation will start after the method is called, additionally an AuthRequest will be sent
   */
  loginUser() {
    this.submitted = true;
    if (this.loginForm.valid) {
      const authRequest: AuthRequest = new AuthRequest(this.loginForm.controls.email.value, this.loginForm.controls.password.value);
      this.authenticateUser(authRequest);
    } else {
      this.messageService.add({severity: 'error', summary: 'Error', detail: 'Invalid input'})
      console.warn('Invalid input');
    }
  }

  /**
   * Send authentication data to the authService. If the authentication was successfully, the user will be forwarded to the message page
   *
   * @param authRequest authentication data from the user login form
   */
  authenticateUser(authRequest: AuthRequest) {
    this.authService.loginUser(authRequest).subscribe({
      next: () => {
        localStorage.setItem('userEmail', authRequest.email);
        this.router.navigate(['/home']);
      },
      error: error => {
        this.printError(error)
      }
    });
  }

  printError(error): void {
    if (error && error.error && error.error.errors) {
      for (let i = 0; i < error.error.errors.length; i++) {
        this.messageService.add({severity: 'error', summary: 'Error', detail: `${error.error.errors[i]}`});
      }
    } else if (error && error.error && error.error.message) {
      this.messageService.add({severity: 'error', summary: 'Error', detail: `${error.error.message}`});
    } else if (error && error.error && error.error.detail) {
      this.messageService.add({severity: 'error', summary: 'Error', detail: `${error.error.detail}`});
    } else if (error && error.status === 403){
      this.messageService.add({severity: 'error', summary: 'Invalid credentials', detail: `Email or Password is incorrect!`});
    } else if (error && error.status === 404){
      this.messageService.add({severity: 'error', summary: 'Invalid credentials', detail: `Email or Password is incorrect!`});
    } else {
        this.messageService.add({severity: 'error', summary: 'Error', detail: `Could not load Recipe!`});
      }
  }
}
