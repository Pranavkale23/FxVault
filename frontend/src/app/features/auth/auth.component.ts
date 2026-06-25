import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../core/services/auth.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-auth',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './auth.component.html',
  styleUrl: './auth.component.css'
})
export class AuthComponent {
  isLogin = true;
  email = '';
  password = '';
  error = '';
  loading = false;

  private authService = inject(AuthService);
  private router = inject(Router);

  toggleMode() {
    this.isLogin = !this.isLogin;
    this.error = '';
  }

  onSubmit() {
    if (!this.email || !this.password) {
      this.error = 'Please fill in all fields';
      return;
    }

    this.loading = true;
    this.error = '';

    const req$ = this.isLogin 
      ? this.authService.login({ email: this.email, password: this.password })
      : this.authService.register({ email: this.email, password: this.password });

    req$.subscribe({
      next: () => {
        this.loading = false;
        this.router.navigate(['/dashboard']);
      },
      error: (err) => {
        this.loading = false;
        this.error = err.error || 'An error occurred';
      }
    });
  }
}
