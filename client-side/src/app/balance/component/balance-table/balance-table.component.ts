import { Component, OnInit, ViewChild } from '@angular/core';
import { MatPaginator } from '@angular/material/paginator';
import { MatTable, MatTableDataSource } from '@angular/material/table';
import { map, Observable } from 'rxjs';
import { UserBalance } from '../../model/user-balance';
import { BalanceService } from '../../service/balance.service';

@Component({
  selector: 'app-balance-table',
  templateUrl: './balance-table.component.html',
  styleUrls: ['./balance-table.component.scss']
})
export class BalanceTableComponent implements OnInit {

  public data$ = this.balanceService.data$;
  public dataSource$: Observable<MatTableDataSource<UserBalance>>;
  public rows: UserBalance[];

  amountData: any;
  showDetails = 0;

  @ViewChild('paginator') paginator: MatPaginator;
  @ViewChild('table') table: MatTable<UserBalance>;

  constructor(private balanceService: BalanceService) { }

  ngOnInit(): void {
    this.dataSource$ = this.balanceService.getWalletsBalance().pipe(
      map(data => new MatTableDataSource(data.balance)),
    );

    this.data$.subscribe(data => {
      this.amountData = [data.symbol, data.total.totalQuantity, "$" + data.total.totalBalance];
    });

    this.balanceService.data$.subscribe(data =>
      this.rows = data.balance
    );

    this.toggleView();
  }

  toggleView() {
    if (this.showDetails === 3) {
      this.showDetails = 0;
    }

    this.showDetails += 1;
  }

  getColor(currentBalance: number, previousBalance: number) {
    return previousBalance < currentBalance ? '#FF4D00' : '#07F36B';
  }

  getNumberWallets(): number {
    return this.rows.length;
  }
}
