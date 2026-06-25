import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private http = inject(HttpClient);
  private apiUrl = '/api/auth';
  private tokenKey = 'auth_token';
  private emailKey = 'user_email';

  register(credentials: any): Observable<any> {
    return this.http.post(`${this.apiUrl}/register`, credentials).pipe(
      tap((res: any) => {
        if (res.token) {
          this.setSession(res.token, res.email);
        }
      })
    );
  }

  login(credentials: any): Observable<any> {
    return this.http.post(`${this.apiUrl}/login`, credentials).pipe(
      tap((res: any) => {
        if (res.token) {
          this.setSession(res.token, res.email);
        }
      })
    );
  }

  logout(): void {
    localStorage.removeItem(this.tokenKey);
    localStorage.removeItem(this.emailKey);
  }

  getToken(): string | null {
    return localStorage.getItem(this.tokenKey);
  }

  getUserEmail(): string | null {
    return localStorage.getItem(this.emailKey);
  }

  isAuthenticated(): boolean {
    return !!this.getToken();
  }

  private setSession(token: string, email: string): void {
    localStorage.setItem(this.tokenKey, token);
    localStorage.setItem(this.emailKey, email);
  }
}
