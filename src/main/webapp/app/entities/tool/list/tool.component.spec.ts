import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { of } from 'rxjs';

import { ToolService } from '../service/tool.service';

import { ToolComponent } from './tool.component';

describe('Tool Management Component', () => {
  let comp: ToolComponent;
  let fixture: ComponentFixture<ToolComponent>;
  let service: ToolService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      declarations: [ToolComponent],
    })
      .overrideTemplate(ToolComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(ToolComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(ToolService);

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
    expect(comp.tools?.[0]).toEqual(expect.objectContaining({ id: 123 }));
  });
});
