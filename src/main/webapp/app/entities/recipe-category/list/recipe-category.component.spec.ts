import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { of } from 'rxjs';

import { RecipeCategoryService } from '../service/recipe-category.service';

import { RecipeCategoryComponent } from './recipe-category.component';

describe('RecipeCategory Management Component', () => {
  let comp: RecipeCategoryComponent;
  let fixture: ComponentFixture<RecipeCategoryComponent>;
  let service: RecipeCategoryService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      declarations: [RecipeCategoryComponent],
    })
      .overrideTemplate(RecipeCategoryComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(RecipeCategoryComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(RecipeCategoryService);

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
    expect(comp.recipeCategories?.[0]).toEqual(expect.objectContaining({ id: 123 }));
  });
});
