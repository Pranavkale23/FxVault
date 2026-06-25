import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { WalletService } from '../../core/services/wallet.service';

@Component({
  selector: 'app-wallet-list',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './wallet-list.component.html',
  styleUrl: './wallet-list.component.css'
})
export class WalletListComponent implements OnInit {
  wallets: any[] = [];
  private walletService = inject(WalletService);
  loading = true;
  newCurrency = 'USD';
  creating = false;

  ngOnInit(): void {
    this.loadWallets();
  }

  loadWallets() {
    this.loading = true;
    this.walletService.getWallets().subscribe({
      next: (data) => {
        this.wallets = data;
        this.loading = false;
      },
      error: () => {
        this.loading = false;
      }
    });
  }

  createWallet() {
    this.creating = true;
    this.walletService.createWallet(this.newCurrency).subscribe({
      next: () => {
        this.creating = false;
        this.loadWallets();
      },
      error: (err) => {
        this.creating = false;
        if (err.status === 400) {
          this.loadWallets();
        } else {
          alert('Something went wrong creating wallet: ' + JSON.stringify(err));
        }
      }
    });
  }

  depositFunds(walletId: number) {
    this.walletService.deposit(walletId, 500).subscribe({
      next: () => {
        this.loadWallets();
        alert('Successfully deposited 500 into wallet!');
      },
      error: () => {
        alert('Deposit failed.');
      }
    });
  }
}
