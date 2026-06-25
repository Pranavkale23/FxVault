import { Component, EventEmitter, inject, Output, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { WalletService } from '../../core/services/wallet.service';

@Component({
  selector: 'app-transfer',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './transfer.component.html',
  styleUrl: './transfer.component.css'
})
export class TransferComponent implements OnInit {
  @Output() transferComplete = new EventEmitter<void>();

  private walletService = inject(WalletService);

  wallets: any[] = [];
  sourceWalletId: string = '';
  targetWalletId: string = '';
  amount: number | null = null;

  loading = false;
  error = '';
  success = '';

  ngOnInit() {
    this.loadWallets();
  }

  loadWallets() {
    this.walletService.getWallets().subscribe({
      next: (data) => {
        this.wallets = data;
        if (this.wallets.length > 0) {
          this.sourceWalletId = this.wallets[0].id.toString();
        }
      }
    });
  }

  onSubmit() {
    if (!this.sourceWalletId || !this.targetWalletId || !this.amount) {
      this.error = 'Please fill all fields';
      return;
    }

    if (this.sourceWalletId === this.targetWalletId) {
      this.error = 'Source and target wallets must be different';
      return;
    }

    this.loading = true;
    this.error = '';
    this.success = '';

    const req = {
      sourceWalletId: Number(this.sourceWalletId),
      targetWalletId: Number(this.targetWalletId),
      amount: this.amount
    };

    this.walletService.transfer(req).subscribe({
      next: () => {
        this.loading = false;
        this.success = 'Transfer completed successfully!';
        this.amount = null;
        this.transferComplete.emit();
        setTimeout(() => this.success = '', 3000);
      },
      error: (err) => {
        this.loading = false;
        this.error = err.error || 'Transfer failed';
      }
    });
  }
}
