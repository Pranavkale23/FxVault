import { Component, OnInit, OnDestroy, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { WalletService } from '../../core/services/wallet.service';
import { Subscription, timer } from 'rxjs';
import { switchMap } from 'rxjs/operators';

interface FxRate {
  id: number;
  sourceCurrency: string;
  targetCurrency: string;
  rate: number;
  lastUpdated: string;
  status?: 'up' | 'down' | 'same'; // for UI coloring
}

@Component({
  selector: 'app-fx-ticker',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './fx-ticker.component.html',
  styleUrl: './fx-ticker.component.css'
})
export class FxTickerComponent implements OnInit, OnDestroy {
  private walletService = inject(WalletService);
  private sub?: Subscription;
  
  rates: FxRate[] = [];

  ngOnInit(): void {
    this.sub = timer(0, 5000).pipe(
      switchMap(() => this.walletService.getLiveRates())
    ).subscribe({
      next: (newRates: FxRate[]) => {
        if (this.rates.length === 0) {
          this.rates = newRates.map(r => ({ ...r, status: 'same' }));
        } else {
          // Compare with existing to determine up/down status
          this.rates = newRates.map(newRate => {
            const oldRate = this.rates.find(r => r.id === newRate.id);
            let status: 'up' | 'down' | 'same' = 'same';
            if (oldRate) {
              if (newRate.rate > oldRate.rate) status = 'up';
              else if (newRate.rate < oldRate.rate) status = 'down';
            }
            return { ...newRate, status };
          });
        }
      }
    });
  }

  ngOnDestroy(): void {
    this.sub?.unsubscribe();
  }
}
