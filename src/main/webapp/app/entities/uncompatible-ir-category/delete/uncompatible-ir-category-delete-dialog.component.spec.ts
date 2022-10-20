jest.mock('@ng-bootstrap/ng-bootstrap');

import { ComponentFixture, TestBed, inject, fakeAsync, tick } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { of } from 'rxjs';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { UncompatibleIRCategoryService } from '../service/uncompatible-ir-category.service';

import { UncompatibleIRCategoryDeleteDialogComponent } from './uncompatible-ir-category-delete-dialog.component';

describe('UncompatibleIRCategory Management Delete Component', () => {
  let comp: UncompatibleIRCategoryDeleteDialogComponent;
  let fixture: ComponentFixture<UncompatibleIRCategoryDeleteDialogComponent>;
  let service: UncompatibleIRCategoryService;
  let mockActiveModal: NgbActiveModal;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      declarations: [UncompatibleIRCategoryDeleteDialogComponent],
      providers: [NgbActiveModal],
    })
      .overrideTemplate(UncompatibleIRCategoryDeleteDialogComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(UncompatibleIRCategoryDeleteDialogComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(UncompatibleIRCategoryService);
    mockActiveModal = TestBed.inject(NgbActiveModal);
  });

  describe('confirmDelete', () => {
    it('Should call delete service on confirmDelete', inject(
      [],
      fakeAsync(() => {
        // GIVEN
        jest.spyOn(service, 'delete').mockReturnValue(of(new HttpResponse({ body: {} })));

        // WHEN
        comp.confirmDelete(123);
        tick();

        // THEN
        expect(service.delete).toHaveBeenCalledWith(123);
        expect(mockActiveModal.close).toHaveBeenCalledWith('deleted');
      })
    ));

    it('Should not call delete service on clear', () => {
      // GIVEN
      jest.spyOn(service, 'delete');

      // WHEN
      comp.cancel();

      // THEN
      expect(service.delete).not.toHaveBeenCalled();
      expect(mockActiveModal.close).not.toHaveBeenCalled();
      expect(mockActiveModal.dismiss).toHaveBeenCalled();
    });
  });
});
