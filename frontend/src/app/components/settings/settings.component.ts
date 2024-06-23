import { Component, OnInit } from '@angular/core';
import { MessageService } from 'primeng/api';
import { AuthService } from 'src/app/services/auth.service';

@Component({
  selector: 'app-settings',
  templateUrl: './settings.component.html',
  styleUrl: './settings.component.scss'
})
export class SettingsComponent implements OnInit {

  isChangePasswordDialogVisible: boolean = false;

  constructor(
    private authService: AuthService,
    private messageService: MessageService
  ) {}

  email: string = "";
  currentPassword: string = "";
  newPassword: string = "";
  confirmPassword: string = "";

  ngOnInit(): void {
    this.email = this.authService.getEmail();
  }

  openChangePasswordDialog(): void {
    this.isChangePasswordDialogVisible = true;
  }

  resetFields(): void {
    this.currentPassword = "";
    this.newPassword = "";
    this.confirmPassword = "";
  }

  changePassword(): void {

    const errors = [];
    if (this.currentPassword === "") {
      errors.push('Current password is required');
    }
    if (!/[a-z]/g.test(this.newPassword)) {
      errors.push('Password must contain at least one lowercase letter');
    }
    if (!/[A-Z]/g.test(this.newPassword)) {
      errors.push('Password must contain at least one uppercase letter');
    }
    if (!/[0-9]/g.test(this.newPassword)) {
      errors.push('Password must contain at least one number');
    }
    if (this.newPassword.length < 8) {
      errors.push('Password must be at least 8 characters long');
    }
    if (this.newPassword !== this.confirmPassword) {
      errors.push('Passwords do not match');
    }

    if (errors.length > 0) {
      errors.forEach(error => this.messageService.add({severity: 'warn', summary: 'Change password failed', detail: error}));
      return;
    }

    this.authService.changePassword({currentPassword: this.currentPassword, newPassword: this.newPassword}).subscribe({
      next: () => {
        this.messageService.add({severity: 'success', summary: 'Change password successful', detail: 'Password successfully changed'});
        this.isChangePasswordDialogVisible = false;
        this.currentPassword = "";
        this.newPassword = "";
        this.confirmPassword = "";
      },
      error: error => {
        if (error.status === 403) {
          this.messageService.add({severity: 'warn', summary: 'Change password failed', detail: 'Current password is incorrect'});
        } else {
          this.messageService.add({severity: 'warn', summary: 'Change password failed', detail: JSON.stringify(error)});
        }
      }
    });

  }

}
