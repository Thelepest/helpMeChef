import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { IUncompatibleIRCategory } from '../uncompatible-ir-category.model';
import { UncompatibleIRCategoryService } from '../service/uncompatible-ir-category.service';
import { UncompatibleIRCategoryDeleteDialogComponent } from '../delete/uncompatible-ir-category-delete-dialog.component';

@Component({
  selector: 'jhi-uncompatible-ir-category',
  templateUrl: './uncompatible-ir-category.component.html',
})
export class UncompatibleIRCategoryComponent implements OnInit {
  uncompatibleIRCategories?: IUncompatibleIRCategory[];
  isLoading = false;

  constructor(protected uncompatibleIRCategoryService: UncompatibleIRCategoryService, protected modalService: NgbModal) {}

  loadAll(): void {
    this.isLoading = true;

    this.uncompatibleIRCategoryService.query().subscribe({
      next: (res: HttpResponse<IUncompatibleIRCategory[]>) => {
        this.isLoading = false;
        this.uncompatibleIRCategories = res.body ?? [];
      },
      error: () => {
        this.isLoading = false;
      },
    });
  }

  ngOnInit(): void {
    this.loadAll();
  }

  trackId(_index: number, item: IUncompatibleIRCategory): number {
    return item.id!;
  }

  delete(uncompatibleIRCategory: IUncompatibleIRCategory): void {
    const modalRef = this.modalService.open(UncompatibleIRCategoryDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.uncompatibleIRCategory = uncompatibleIRCategory;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed.subscribe(reason => {
      if (reason === 'deleted') {
        this.loadAll();
      }
    });
  }
}
