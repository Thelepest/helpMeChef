import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { MyConfigService } from '../service/my-config.service';
import { IMyConfig, MyConfig } from '../my-config.model';

import { MyConfigUpdateComponent } from './my-config-update.component';

describe('MyConfig Management Update Component', () => {
  let comp: MyConfigUpdateComponent;
  let fixture: ComponentFixture<MyConfigUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let myConfigService: MyConfigService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [MyConfigUpdateComponent],
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
      .overrideTemplate(MyConfigUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(MyConfigUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    myConfigService = TestBed.inject(MyConfigService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should update editForm', () => {
      const myConfig: IMyConfig = { id: 456 };

      activatedRoute.data = of({ myConfig });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(myConfig));
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<MyConfig>>();
      const myConfig = { id: 123 };
      jest.spyOn(myConfigService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ myConfig });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: myConfig }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(myConfigService.update).toHaveBeenCalledWith(myConfig);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<MyConfig>>();
      const myConfig = new MyConfig();
      jest.spyOn(myConfigService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ myConfig });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: myConfig }));
      saveSubject.complete();

      // THEN
      expect(myConfigService.create).toHaveBeenCalledWith(myConfig);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<MyConfig>>();
      const myConfig = { id: 123 };
      jest.spyOn(myConfigService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ myConfig });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(myConfigService.update).toHaveBeenCalledWith(myConfig);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
