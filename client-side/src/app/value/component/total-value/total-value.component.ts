import { Component, OnInit } from '@angular/core';
import { Observable } from 'rxjs';
import { TotalValueService } from '../../service/total-value.service';

@Component({
  selector: 'app-total-value',
  templateUrl: './total-value.component.html',
  styleUrls: ['./total-value.component.scss']
})
export class TotalValueComponent implements OnInit {

  public data: number;

  constructor(private totalValueService: TotalValueService) { }
  
  ngOnInit(): void {
    this.totalValueService.currentTotalValue$.subscribe(data => {
      this.data = data;
    });
  }
}
