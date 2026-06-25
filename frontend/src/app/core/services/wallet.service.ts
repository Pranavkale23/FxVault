import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class WalletService {
  private http = inject(HttpClient);
  private apiUrl = '/api/wallets';

  getWallets(): Observable<any[]> {
    return this.http.get<any[]>(this.apiUrl);
  }

  getTotalBalance(): Observable<{totalBalanceUSD: number}> {
    return this.http.get<{totalBalanceUSD: number}>(`${this.apiUrl}/total`);
  }

  getLiveRates(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/rates`);
  }

  createWallet(currency: string): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/create`, { currency });
  }

  deposit(walletId: number, amount: number): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/deposit`, { walletId, amount });
  }

  transfer(transferReq: any): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/transfer`, transferReq);
  }

  splitTransfer(splitReq: any): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/split-transfer`, splitReq);
  }
}
