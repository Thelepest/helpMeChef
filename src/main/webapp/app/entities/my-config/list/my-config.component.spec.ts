import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { of } from 'rxjs';

import { MyConfigService } from '../service/my-config.service';

import { MyConfigComponent } from './my-config.component';

describe('MyConfig Management Component', () => {
  let comp: MyConfigComponent;
  let fixture: ComponentFixture<MyConfigComponent>;
  let service: MyConfigService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      declarations: [MyConfigComponent],
    })
      .overrideTemplate(MyConfigComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(MyConfigComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(MyConfigService);

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
    expect(comp.myConfigs?.[0]).toEqual(expect.objectContaining({ id: 123 }));
  });
});
