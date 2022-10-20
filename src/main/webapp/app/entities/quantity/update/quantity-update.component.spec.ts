import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { QuantityService } from '../service/quantity.service';
import { IQuantity, Quantity } from '../quantity.model';

import { QuantityUpdateComponent } from './quantity-update.component';

describe('Quantity Management Update Component', () => {
  let comp: QuantityUpdateComponent;
  let fixture: ComponentFixture<QuantityUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let quantityService: QuantityService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [QuantityUpdateComponent],
      providers: [
        FormBuilder,
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    })
      .overrideTemplate(QuantityUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(QuantityUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    quantityService = TestBed.inject(QuantityService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should update editForm', () => {
      const quantity: IQuantity = { id: 456 };

      activatedRoute.data = of({ quantity });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(quantity));
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Quantity>>();
      const quantity = { id: 123 };
      jest.spyOn(quantityService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ quantity });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: quantity }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(quantityService.update).toHaveBeenCalledWith(quantity);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Quantity>>();
      const quantity = new Quantity();
      jest.spyOn(quantityService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ quantity });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: quantity }));
      saveSubject.complete();

      // THEN
      expect(quantityService.create).toHaveBeenCalledWith(quantity);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Quantity>>();
      const quantity = { id: 123 };
      jest.spyOn(quantityService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ quantity });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(quantityService.update).toHaveBeenCalledWith(quantity);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
