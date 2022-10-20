import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { of } from 'rxjs';

import { IngredientService } from '../service/ingredient.service';

import { IngredientComponent } from './ingredient.component';

describe('Ingredient Management Component', () => {
  let comp: IngredientComponent;
  let fixture: ComponentFixture<IngredientComponent>;
  let service: IngredientService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      declarations: [IngredientComponent],
    })
      .overrideTemplate(IngredientComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(IngredientComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(IngredientService);

    const headers = new HttpHeaders();
    jest.spyOn(service, 'query').mockReturnValue(
      of(
        new HttpResponse({
          body: [{ id: 123 }],
          headers,
        })
      )
    );
  });

  it('Should call load all on init', () => {
    // WHEN
    comp.ngOnInit();

    // THEN
    expect(service.query).toHaveBeenCalled();
    expect(comp.ingredients?.[0]).toEqual(expect.objectContaining({ id: 123 }));
  });
});
