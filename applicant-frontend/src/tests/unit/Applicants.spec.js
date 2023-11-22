import { mount } from '@vue/test-utils';
import Applicants from '@/components/Applicants.vue';
import axios from 'axios';

jest.mock('axios');

describe('Applicants.vue', () => {
  let wrapper;

  beforeEach(() => {
    axios.get.mockResolvedValue({ data: [] });
    axios.post.mockResolvedValue({ data: {} });
    axios.patch.mockResolvedValue({ data: {} });

    wrapper = mount(Applicants);
  });

  it('renders applicants correctly', async () => {
    await wrapper.vm.$nextTick();
    expect(wrapper.contains('table')).toBe(true);
  });

  it('adds an applicant', async () => {
    wrapper.setData({ newApplicantName: 'John Doe' });
    await wrapper.vm.addApplicant();
    expect(axios.post).toHaveBeenCalledWith(`${APPLICANT_API_BASE_URL}/applicants`, { name: 'John Doe' }, expect.any(Object));
  });

  it('updates applicant status', async () => {
    await wrapper.vm.$nextTick();
    const openApplicant = { _id: '1', name: 'John Doe', status: 'OPEN' };
    wrapper.setData({ applicants: [openApplicant] });
    await wrapper.vm.updateApplicantStatus('1', 'ACCEPTED');
    expect(axios.patch).toHaveBeenCalledWith(`${APPLICANT_API_BASE_URL}/applicants?id=1`, '"ACCEPTED"', expect.any(Object));
    expect(wrapper.vm.applicants[0].status).toBe('ACCEPTED');
  });
});