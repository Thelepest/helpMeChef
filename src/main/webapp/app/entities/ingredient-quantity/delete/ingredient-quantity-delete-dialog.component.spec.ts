jest.mock('@ng-bootstrap/ng-bootstrap');

import { ComponentFixture, TestBed, inject, fakeAsync, tick } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { of } from 'rxjs';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IngredientQuantityService } from '../service/ingredient-quantity.service';

import { IngredientQuantityDeleteDialogComponent } from './ingredient-quantity-delete-dialog.component';

describe('IngredientQuantity Management Delete Component', () => {
  let comp: IngredientQuantityDeleteDialogComponent;
  let fixture: ComponentFixture<IngredientQuantityDeleteDialogComponent>;
  let service: IngredientQuantityService;
  let mockActiveModal: NgbActiveModal;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      declarations: [IngredientQuantityDeleteDialogComponent],
      providers: [NgbActiveModal],
    })
      .overrideTemplate(IngredientQuantityDeleteDialogComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(IngredientQuantityDeleteDialogComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(IngredientQuantityService);
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
