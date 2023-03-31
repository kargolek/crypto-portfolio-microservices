import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import { TotalValue } from '../model/total-value';

@Injectable({
  providedIn: 'root'
})
export class TotalValueService {

  private totalNum: number[] = [];
  private totalValueSubject = new BehaviorSubject<number>(0);
  public currentTotalValue$ = this.totalValueSubject.asObservable();

  constructor() { }

  setTotalValue(total: number) {
    this.totalNum.push(total);
    if (this.totalNum.length === 3) {
      let totalValue = 0;
      this.totalNum.forEach(element => {
        totalValue += element;
      });
      this.totalNum = [];
      this.totalValueSubject.next(totalValue);
    }
  }
}
