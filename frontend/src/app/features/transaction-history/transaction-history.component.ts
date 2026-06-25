import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { TransactionService } from '../../core/services/transaction.service';

@Component({
  selector: 'app-transaction-history',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './transaction-history.component.html',
  styleUrl: './transaction-history.component.css'
})
export class TransactionHistoryComponent implements OnInit {
  transactions: any[] = [];
  loading = true;

  private transactionService = inject(TransactionService);

  ngOnInit() {
    this.loadHistory();
  }

  loadHistory() {
    this.loading = true;
    this.transactionService.getHistory().subscribe({
      next: (data) => {
        this.transactions = data;
        this.loading = false;
      },
      error: () => {
        this.loading = false;
      }
    });
  }
}
