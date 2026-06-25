import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { WalletListComponent } from '../wallet-list/wallet-list.component';
import { TransferComponent } from '../transfer/transfer.component';
import { TransactionHistoryComponent } from '../transaction-history/transaction-history.component';
import { WalletService } from '../../core/services/wallet.service';

import { FxTickerComponent } from '../fx-ticker/fx-ticker.component';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, WalletListComponent, TransferComponent, TransactionHistoryComponent, FxTickerComponent],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.css'
})
export class DashboardComponent implements OnInit {
  totalBalanceUSD: number = 0;
  private walletService = inject(WalletService);

  ngOnInit(): void {
    this.calculateTotalBalance();
  }

  calculateTotalBalance() {
    this.walletService.getTotalBalance().subscribe({
      next: (res) => {
        this.totalBalanceUSD = res.totalBalanceUSD;
      }
    });
  }

  onTransferComplete() {
    this.calculateTotalBalance();
  }
}
