import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { ToolService } from '../service/tool.service';
import { ITool, Tool } from '../tool.model';

import { ToolUpdateComponent } from './tool-update.component';

describe('Tool Management Update Component', () => {
  let comp: ToolUpdateComponent;
  let fixture: ComponentFixture<ToolUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let toolService: ToolService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [ToolUpdateComponent],
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
      .overrideTemplate(ToolUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(ToolUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    toolService = TestBed.inject(ToolService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should update editForm', () => {
      const tool: ITool = { id: 456 };

      activatedRoute.data = of({ tool });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(tool));
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Tool>>();
      const tool = { id: 123 };
      jest.spyOn(toolService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ tool });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: tool }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(toolService.update).toHaveBeenCalledWith(tool);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Tool>>();
      const tool = new Tool();
      jest.spyOn(toolService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ tool });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: tool }));
      saveSubject.complete();

      // THEN
      expect(toolService.create).toHaveBeenCalledWith(tool);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Tool>>();
      const tool = { id: 123 };
      jest.spyOn(toolService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ tool });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(toolService.update).toHaveBeenCalledWith(tool);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
