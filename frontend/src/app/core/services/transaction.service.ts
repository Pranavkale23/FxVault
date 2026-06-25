import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class TransactionService {
  private http = inject(HttpClient);
  private apiUrl = '/api/transactions';

  getHistory(): Observable<any[]> {
    return this.http.get<any[]>(this.apiUrl);
  }
}
